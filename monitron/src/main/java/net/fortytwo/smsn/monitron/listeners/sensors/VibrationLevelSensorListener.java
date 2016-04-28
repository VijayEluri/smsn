package net.fortytwo.smsn.monitron.listeners.sensors;

import net.fortytwo.smsn.monitron.Context;
import net.fortytwo.smsn.monitron.data.GaussianData;
import net.fortytwo.smsn.monitron.events.MonitronEvent;
import net.fortytwo.smsn.monitron.events.VibrationLevelObservation;
import org.openrdf.model.URI;

/**
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class VibrationLevelSensorListener extends GaussianSensorListener {
    public VibrationLevelSensorListener(final Context context,
                                        final URI sensor) {
        super(context, sensor);
    }

    protected MonitronEvent handleSample(final GaussianData data) {
        return new VibrationLevelObservation(context, sensor, data);
    }
}