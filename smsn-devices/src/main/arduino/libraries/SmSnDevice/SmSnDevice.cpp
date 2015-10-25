/*
  SmSnDevice.cpp
  Created by Joshua Shinavier, 2014-2015
  Released into the public domain.
*/

#include "SmSn.h"
#include "SmSnDevice.h"
#include <RGBLED.h>

#define OSC_CONTEXT_SET  "/context/set"
#define OSC_ERROR        "/error"
#define OSC_HEARTBEAT    "/heartbeat"
#define OSC_INFO         "/info"
#define OSC_OK           "/ok"
#define OSC_PING         "/ping"
#define OSC_PING_REPLY   "/ping/reply"
#define OSC_READY        "/ready"
#define OSC_RGB_SET      "/rgb/set"
#define OSC_TONE         "/tone"
#define OSC_VIBRO        "/vibro"
#define OSC_WARNING      "/warning"

#ifdef BOUNTIFUL_RAM
#define ERR_COLOR_OUT_OF_RANGE "color out of range: %d"
#define ERR_FREQ_OUT_OF_RANGE "frequency out of range: %d"
#define ERR_DURATION_NONPOS "duration must be a positive number"
#define ERR_DURATION_TOO_LONG "duration too long"
#define ERR_EMPTY_OSC_BUNDLE "empty OSC bundle"
#define ERR_NO_HANDLER "no handler at address %s"
#else
const char *error_msg = "error";
#define ERR_COLOR_OUT_OF_RANGE error_msg
#define ERR_FREQ_OUT_OF_RANGE error_msg
#define ERR_DURATION_NONPOS error_msg
#define ERR_DURATION_TOO_LONG error_msg
#define ERR_EMPTY_OSC_BUNDLE error_msg
#define ERR_NO_HANDLER error_msg
#endif // BOUNTIFUL_RAM


const unsigned long connectionRetryIntervalMs = 2000;

// allows OSC to dispatch messages to non-member functions which call member functions
SmSnDevice *thisDevice;

SmSnDevice::SmSnDevice(const char *oscPrefix): osc(oscPrefix) {
    thisDevice = this;
    ledCueLength = 0;
    ledCueSince = 0;
    lastHeartbeat = 0;
    loopTimeHandler = NULL;
    setContext("default");
    inputEnabled = true;
}

const char* SmSnDevice::getContext() {
    return contextName;
}

void SmSnDevice::setContext(const char *context) {
    strcpy(contextName, context);
}

/*
Morse *SmSnDevice::getMorse() {
    return morse;
}
*/

/*
Droidspeak *SmSnDevice::getDroidspeak() {
    return droidspeak;
}
*/


////////////////////////////////////////////////////////////////////////////////
// setup

void SmSnDevice::setup() {
    setupPins();

    setColor(RGB_YELLOW);

    //morse = createMorse();

/*
    droidspeak = createDroidspeak();
    if (droidspeak) {
        droidspeak->speakPowerUpPhrase();
    }
*/

    osc.beginSerial();

    bundleIn = new OSCBundle();

    setupOther();

    // delay the serial open phrase until the random number generator has been seeded
    setColor(RGB_GREEN);
    /*
    if (droidspeak) {
        droidspeak->speakSerialOpenPhrase();
    }
    */
    setColor(RGB_BLACK);

    vibrate(500);
    osc.sendInfo("%s is ready", osc.getPrefix());

    loopCount = 0;
    loopThreshold = 100;
    // initial value for loop time gets us through the first half second or so
    loopTime = 4.0/1000;
    loopStartTime = millis();
}


////////////////////////////////////////////////////////////////////////////////
// looping

void SmSnDevice::setLoopTimeHandler(void (*handler)(double)) {
    loopTimeHandler = handler;
}

double SmSnDevice::getLoopTime() {
    return loopTime;
}

unsigned long SmSnDevice::beginLoop() {
    if (inputEnabled) {
        if (osc.receiveOSCBundle(*bundleIn)) {
            handleOSCBundleInternal(*bundleIn);
            bundleIn->empty();
            delete bundleIn;
            bundleIn = new OSCBundle();
        }
    }

    // no need for micros() here, and it's simpler to avoid overflow
    unsigned long now = millis();

    checkLedStatus(now);
#ifdef BOUNTIFUL_RAM
    checkConnection(now);
#endif
    onBeginLoop(now);

// TODO: never applies
#ifdef HEARTBEAT_MS
    if (now - lastHeartbeat > HEARTBEAT_MS) {
        sendHeartbeatMessage(now);
        lastHeartbeat = now;
    }
#endif // HEARTBEAT_MS

    // periodically adjust the loop time used in band-pass filtering (among potentially other applications)
    if (++loopCount >= loopThreshold) {
        loopTime = (now - loopStartTime)/1000.0/loopThreshold;

        onLoopTimeUpdated(loopTime);

        if (loopTimeHandler) {
            loopTimeHandler(loopTime);
        }

        loopCount = 0;
        loopStartTime = now;
    }

    return now;
}


////////////////////////////////////////////////////////////////////////////////
// cues

void SmSnDevice::setColorFor(unsigned long color, unsigned long durationMs) {
    setColor(color);
    ledCueLength = durationMs;
    ledCueSince = millis();
}

void SmSnDevice::checkLedStatus(unsigned long now) {
    if (ledCueSince > 0 && now - ledCueSince > ledCueLength) {
        setColor(RGB_BLACK);
        ledCueSince = 0;
    }
}

void SmSnDevice::errorCue() {
    setColorFor(RGB_RED, errorCueVisualDurationMs);
}

void SmSnDevice::infoCue() {
    setColorFor(RGB_BLUE, infoCueVisualDurationMs);
}

void SmSnDevice::okCue() {
    setColorFor(RGB_GREEN, okCueVisualDurationMs);
}

void SmSnDevice::readyCue() {
    setColorFor(RGB_WHITE, readyCueVisualDurationMs);
}

void SmSnDevice::warningCue() {
    setColorFor(RGB_YELLOW, warningCueVisualDurationMs);
}


////////////////////////////////////////////////////////////////////////////////
// OSC

void SmSnDevice::enableInput(boolean b) {
    inputEnabled = b;
}

SmSnOsc *SmSnDevice::getOSC() {
    return &osc;
}

const char *SmSnDevice::address(const char *suffix) {
    // note: redundant writing of osc.getPrefix() to the buffer
    sprintf(oscAddressBuffer, "%s%s", osc.getPrefix(), suffix);
    return oscAddressBuffer;
}

void SmSnDevice::sendPingReply() {
    OSCMessage m(address(OSC_PING_REPLY));
    m.add((uint64_t) micros());

    osc.sendOSC(m);
}

// TODO: never applies
#ifdef HEARTBEAT_MS
void SmSnDevice::sendHeartbeatMessage(unsigned long now) {
    OSCMessage m(address(OSC_HEARTBEAT));
    m.add((uint64_t) now);
    osc.sendOSC(m);
}
#endif


////////////////////////////////////////////////////////////////////////////////
// non-member OSC handler functions

#ifdef BOUNTIFUL_RAM
void handleContextSetMessage(class OSCMessage &m) {
    if (!thisDevice->getOSC()->validArgs(m, 1)) return;

    char buffer[16];
    m.getString(0, buffer, m.getDataLength(0) + 1);
    thisDevice->setContext(buffer);
}
#endif

void handleErrorMessage(class OSCMessage &m) {
    thisDevice->errorCue();
    thisDevice->vibrate(errorCueHapticDurationMs);
}

void handleInfoMessage(class OSCMessage &m) {
    thisDevice->infoCue();
    thisDevice->vibrate(infoCueHapticDurationMs);
}

/*
const int morseBufferLength = 32;
char morseBuffer[morseBufferLength];

void handleMorseMessage(class OSCMessage &m) {
    if (!thisDevice->getOSC()->validArgs(m, 1)) return;

    if (!thisDevice->getMorse()) {
        thisDevice->getOSC()->sendError("Morse not supported");
        thisDevice->errorCue();
    }

    int length = m.getDataLength(0);
    if (length >= morseBufferLength) {
        thisDevice->getOSC()->sendError("Morse message is too long");
        thisDevice->errorCue();
    } else {
        m.getString(0, morseBuffer, length+1);
        thisDevice->getMorse()->playMorseString((const char*) morseBuffer);
    }
}
*/

void handleOkMessage(class OSCMessage &m) {
    thisDevice->okCue();
}

void handlePingMessage(class OSCMessage &m) {
    thisDevice->sendPingReply();
}

#ifdef BOUNTIFUL_RAM
void handlePingReplyMessage(class OSCMessage &m) {
    thisDevice->confirmConnection();
}
#endif

void handleReadyMessage(class OSCMessage &m) {
    thisDevice->readyCue();
}

// TODO: restore once memory is not so tight on Extend-o-Hand
#ifdef BOUNTIFUL_RAM
void handleRGBSetMessage(class OSCMessage &m) {
    if (!thisDevice->getOSC()->validArgs(m, 1)) return;

    int32_t color = m.getInt(0);

    if (color < 0 || color > 0xffffff) {
        thisDevice->getOSC()->sendError(ERR_COLOR_OUT_OF_RANGE, (long) color);
    } else {
        thisDevice->setColor(color);
    }
}

void handleToneMessage(class OSCMessage &m) {
    if (!thisDevice->getOSC()->validArgs(m, 2)) return;

    int32_t frequency = m.getInt(0);
    int32_t duration = m.getInt(1);

    if (frequency <= 0 || frequency > 20000) {
        thisDevice->getOSC()->sendError(ERR_FREQ_OUT_OF_RANGE, (int) frequency);
    } else if (duration <= 0) {
        thisDevice->getOSC()->sendError(ERR_DURATION_NONPOS);
    } else if (duration > 60000) {
        thisDevice->getOSC()->sendError(ERR_DURATION_TOO_LONG);
    } else {
        thisDevice->playTone((unsigned int) frequency, (unsigned long) duration);
    }
}

void handleVibroMessage(class OSCMessage &m) {
    if (!thisDevice->getOSC()->validArgs(m, 1)) return;

    int32_t d = m.getInt(0);

    thisDevice->vibrate((unsigned long) d);

    if (d <= 0) {
        thisDevice->getOSC()->sendError(ERR_DURATION_NONPOS);
    } else if (d > 60000) {
        thisDevice->getOSC()->sendError(ERR_DURATION_TOO_LONG);
    } else {
        thisDevice->vibrate((unsigned long) d);
    }
}
#endif // BOUNTIFUL_RAM

void handleWarningMessage(class OSCMessage &m) {
    thisDevice->warningCue();
    thisDevice->vibrate(warningCueHapticDurationMs);
}


////////////////////////////////////////////////////////////////////////////////

void SmSnDevice::handleOSCBundleInternal(class OSCBundle &bundle) {
    if (bundle.hasError()) {
        errorCue();
        playTone(400,100);
        osc.sendOSCBundleError(bundle);
    } else if (!(handleOSCBundle(bundle)
        // TODO: copying addresses into buffers on the fly (via address()), one by one, is inefficient
        || bundle.dispatch(address(OSC_ERROR), handleErrorMessage)
        || bundle.dispatch(address(OSC_INFO), handleInfoMessage)
        //|| bundle.dispatch(address(OSC_MORSE), handleMorseMessage)
        || bundle.dispatch(address(OSC_OK), handleOkMessage)
        || bundle.dispatch(address(OSC_PING), handlePingMessage)
        || bundle.dispatch(address(OSC_READY), handleReadyMessage)
#ifdef BOUNTIFUL_RAM
        || bundle.dispatch(address(OSC_CONTEXT_SET), handleContextSetMessage)
        || bundle.dispatch(address(OSC_PING_REPLY), handlePingReplyMessage)
        || bundle.dispatch(address(OSC_RGB_SET), handleRGBSetMessage)
        || bundle.dispatch(address(OSC_TONE), handleToneMessage)
        || bundle.dispatch(address(OSC_VIBRO), handleVibroMessage)
#endif // BOUNTIFUL_RAM
        || bundle.dispatch(address(OSC_WARNING), handleWarningMessage)
        )) {
        if (!bundle.size()) {
            osc.sendError(ERR_EMPTY_OSC_BUNDLE);
        } else {
            for (int i = 0; i < bundle.size(); i++) {
                OSCMessage *m = bundle.getOSCMessage(i);
                char address[64];
                m->getAddress(address);
                osc.sendError(ERR_NO_HANDLER, address);
            }
        }
        errorCue();
    }
}


////////////////////////////////////////////////////////////////////////////////
// connection state

#ifdef BOUNTIFUL_RAM
void SmSnDevice::pingUntilConnected() {
    connected = false;
    connecting = true;
    lastConnectionAttempt = 0;
}

void SmSnDevice::confirmConnection() {
    connected = true;
}

void SmSnDevice::checkConnection(unsigned long now) {
    if (!connecting) {
        return;
    }

    if (connected) {
        connecting = false;
        okCue();
        /*
        if (droidspeak) {
            droidspeak->speakOK();
        }*/
        playTone(1760,100);
        playTone(880,100);
    } else if (now - lastConnectionAttempt > connectionRetryIntervalMs) {
        lastConnectionAttempt = now;
        warningCue();
        OSCMessage m(address(OSC_PING));
        osc.sendOSC(m);
    }
}
#endif // BOUNTIFUL_RAM


