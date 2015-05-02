package com.jcwhatever.tpregions;

import com.jcwhatever.tpregions.regions.TPRegion;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * TPRegions script API object.
 */
public class ScriptApi implements IDisposable {

    private boolean _isDisposed;

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _isDisposed = true;
    }

    public TPRegion get(String name) {
        PreCon.notNullOrEmpty(name);

        TPRegion region = TPRegions.getRegionManager().getRegion(name);
        PreCon.isValid(region != null, "Region named '{0}' not found.", name);

        return region;
    }
}
