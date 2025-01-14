/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.internal.arcsde;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.ResourceInfo;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.util.GeotoolsResourceInfoAdapter;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p>
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
class ArcSDEVectorGeoResource extends IGeoResource {
    String typename = null;

    /**
     * Construct <code>PostGISGeoResource</code>.
     *
     * @param parent
     * @param typename
     */
    public ArcSDEVectorGeoResource(ArcServiceImpl service, String typename) {
        this.service = service;
        this.typename = typename;
    }

    @Override
    public URL getIdentifier() {
        try {
            return new URL(service.getIdentifier().toString() + "#" + typename); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            return service.getIdentifier();
        }
    }

    /**
     * @see org.locationtech.udig.catalog.IGeoResource#getStatus()
     */
    @Override
    public Status getStatus() {
        return service.getStatus();
    }

    /**
     * @see org.locationtech.udig.catalog.IGeoResource#getStatusMessage()
     */
    @Override
    public Throwable getMessage() {
        return service.getMessage();
    }

    /**
     * Required adoptions:
     * <ul>
     * <li>IGeoResourceInfo.class
     * <li>IService.class
     * </ul>
     *
     * @see org.locationtech.udig.catalog.IResolve#resolve(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
        if (adaptee == null)
            return null;
        if (adaptee.isAssignableFrom(IService.class))
            return adaptee.cast(service(monitor));
        if (adaptee.isAssignableFrom(IGeoResource.class))
            return adaptee.cast(this);
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class))
            return adaptee.cast(createInfo(monitor));
        if (adaptee.isAssignableFrom(SimpleFeatureStore.class)) {
            DataStore dataStore = getDataStore(monitor);
            SimpleFeatureSource fs;
            fs = dataStore.getFeatureSource(typename);

            if (fs instanceof SimpleFeatureStore) {
                return adaptee.cast(fs);
            }
            if (adaptee.isAssignableFrom(SimpleFeatureSource.class)) {
                return adaptee.cast(fs);
            }
        }
        return super.resolve(adaptee, monitor);
    }

    public DataStore getDataStore(IProgressMonitor monitor) throws IOException {
        ArcServiceImpl service = service(monitor);
        ArcSDEVectorService vectorService = service.getVectorService();
        DataStore dataStore = vectorService.getDataStore(monitor);
        return dataStore;
    }

    @Override
    public <T> boolean canResolve(Class<T> adaptee) {
        if (adaptee == null) {
            return false;
        }
        return adaptee.isAssignableFrom(IGeoResourceInfo.class)
                || adaptee.isAssignableFrom(SimpleFeatureStore.class)
                || adaptee.isAssignableFrom(SimpleFeatureSource.class) || super.canResolve(adaptee);
    }

    @Override
    protected IGeoResourceInfo createInfo(IProgressMonitor monitor) throws IOException {
        DataStore dataStore = getDataStore(monitor);
        FeatureSource<SimpleFeatureType, SimpleFeature> fs = dataStore.getFeatureSource(typename);
        ResourceInfo gtinfo = fs.getInfo();
        GeotoolsResourceInfoAdapter vectorInfo = new GeotoolsResourceInfoAdapter(gtinfo);
        return vectorInfo;
    }

    @Override
    public ArcServiceImpl service(IProgressMonitor monitor) throws IOException {
        return (ArcServiceImpl) super.service(monitor);
    }
}
