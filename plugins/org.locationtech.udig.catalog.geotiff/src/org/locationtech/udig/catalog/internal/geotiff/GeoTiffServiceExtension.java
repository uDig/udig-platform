/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.internal.geotiff;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ServiceExtension2;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.geotiff.internal.Messages;

import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffFormatFactorySpi;


/**
 * Provides the interface to the catalog service extension point.
 * <p>
 * This class is responsible for ensuring that only those services that the GeoTiff plug-in is
 * capable of processing are created.
 * </p>
 *
 * @author mleslie
 * @since 0.6.0
 */
public class GeoTiffServiceExtension implements ServiceExtension2 {
    /** <code>URL_PARAM</code> field */
    public static final String URL_PARAM = "URL"; //$NON-NLS-1$

    public static final String TYPE = "geotiff"; //$NON-NLS-1$

    private static GeoTiffFormatFactorySpi factory;
    private static GeoTiffFormat format;

    /**
     * Construct <code>GeoTiffServiceExtension</code>.
     */
    public GeoTiffServiceExtension() {
        super();
    }

    public IService createService( URL id, Map<String, Serializable> params ) {
        URL id2 = id;
        if (id2 == null) {
            id2 = extractID(params);
        }
        if (!canProcess(extractID(params))) {
            return null;
        }
        GeoTiffServiceImpl service = new GeoTiffServiceImpl(extractID(params), getFactory());
        return service;
    }

    private URL extractID( Map<String, Serializable> params ) {
        URL id;
        if (params.containsKey(URL_PARAM)) {
            Object param = params.get(URL_PARAM);
            if (param instanceof String) {
                try {
                    id = new URL((String) param);
                } catch (MalformedURLException ex) {
                    return null;
                }
            } else if (param instanceof URL) {
                id = (URL) param;
            } else {
                return null;
            }
        } else {
            return null;
        }
        return id;
    }

    private synchronized static GeoTiffFormat getFormat() {
        if (format == null) {
            format = (GeoTiffFormat) getFactory().createFormat();
        }
        return format;
    }

    /**
     * Finds or creates a GeoTiffFormatFactorySpi.
     *
     * @return Default GeoTiffFormatFactorySpi
     */
    public synchronized static GeoTiffFormatFactorySpi getFactory() {
        if (factory == null) {
            factory = new GeoTiffFormatFactorySpi();
        }
        return factory;
    }

    private boolean canProcess( URL id ) {
        if ( reasonForFailure(id)==null )
            return true;
        return false;
    }

    public Map<String, Serializable> createParams( URL url ) {
        if (!canProcess(url))
            return null;

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        if (url != null) {
            params.put(URL_PARAM, url);
        }
        return params;
    }

    public String reasonForFailure( Map<String, Serializable> params ) {
        return reasonForFailure(extractID(params));
    }

    public String reasonForFailure( URL url ) {
        if (url == null) {
            return Messages.GeoTiffServiceExtension_nullURL;
        }

        if( !isSupportedExtension(url) )
            return Messages.GeoTiffServiceExtension_badExt;

        File file = null;
        try {
            ID id = new ID( url );
            file = id.toFile();
        } catch (IllegalArgumentException ex) {
            return url.toExternalForm()+Messages.GeoTiffServiceExtension_notFile;
        }

        if (!file.exists() )
            return file+Messages.GeoTiffServiceExtension_notExist;


        try {
            if (!getFormat().accepts(file))
                return Messages.GeoTiffServiceExtension_unknown;
        } catch (RuntimeException ex) {
            return Messages.GeoTiffServiceExtension_unknown;
        }
        return null;
    }

    private boolean isSupportedExtension( URL url ) {
        File file = URLUtils.urlToFile(url);
        String fileLower = file.getAbsolutePath().toLowerCase();
        boolean isTiff = fileLower.endsWith(".tiff") || fileLower.endsWith(".tif"); //$NON-NLS-1$ //$NON-NLS-2$
        if (!isTiff) {
            return false;
        }
        return true;
    }


}
