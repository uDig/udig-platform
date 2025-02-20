/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.wmsc.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.geotools.data.ows.Service;
import org.geotools.ows.wms.CRSEnvelope;
import org.geotools.xml.PrintHandler;
import org.geotools.xml.schema.Attribute;
import org.geotools.xml.schema.Element;
import org.geotools.xml.schema.ElementGrouping;
import org.geotools.xml.schema.ElementValue;
import org.geotools.xml.schema.Sequence;
import org.geotools.xml.schema.impl.SequenceGT;
import org.geotools.xml.xsi.XSISimpleTypes;
import org.locationtech.udig.catalog.wmsc.server.WMSCSchema.WMSCAttribute;
import org.locationtech.udig.catalog.wmsc.server.WMSCSchema.WMSCComplexType;
import org.locationtech.udig.catalog.wmsc.server.WMSCSchema.WMSCElement;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * WMS-C getCapabilities XML complex types (part of the capabilities document).
 *
 * @author Emily Gouge (Refractions Research, Inc.)
 * @since 1.1.0
 */
public interface WMSCComplexTypes {

    public static class _BoundingBoxType extends WMSCComplexType {
        private static final WMSCComplexType instance = new _BoundingBoxType();

        private static Attribute[] attrs = new Attribute[] {
                new WMSCAttribute(null, "CRS", WMSCSchema.NAMESPACE, //$NON-NLS-1$
                        XSISimpleTypes.String.getInstance(), Attribute.OPTIONAL, null, null, false),
                new WMSCAttribute(null, "SRS", WMSCSchema.NAMESPACE, //$NON-NLS-1$
                        XSISimpleTypes.String.getInstance(), Attribute.OPTIONAL, null, null, false),
                new WMSCAttribute(null, "minx", WMSCSchema.NAMESPACE, //$NON-NLS-1$
                        XSISimpleTypes.Double.getInstance(), Attribute.REQUIRED, null, null, false),
                new WMSCAttribute(null, "miny", WMSCSchema.NAMESPACE, //$NON-NLS-1$
                        XSISimpleTypes.Double.getInstance(), Attribute.REQUIRED, null, null, false),
                new WMSCAttribute(null, "maxx", WMSCSchema.NAMESPACE, //$NON-NLS-1$
                        XSISimpleTypes.Double.getInstance(), Attribute.REQUIRED, null, null, false),
                new WMSCAttribute(null, "maxy", WMSCSchema.NAMESPACE, //$NON-NLS-1$
                        XSISimpleTypes.Double.getInstance(), Attribute.REQUIRED, null, null, false),
                new WMSCAttribute("resx", XSISimpleTypes.Double.getInstance()), //$NON-NLS-1$
                new WMSCAttribute("resy", XSISimpleTypes.Double.getInstance()) }; //$NON-NLS-1$

        public static WMSCComplexType getInstance() {
            return instance;
        }

        @Override
        public Attribute[] getAttributes() {
            return attrs;
        }

        @Override
        public ElementGrouping getChild() {
            return null;
        }

        @Override
        public Element[] getChildElements() {
            return null;
        }

        @Override
        public Object getValue(Element element, ElementValue[] value, Attributes attrs, Map hints)
                throws SAXException, OperationNotSupportedException {
            CRSEnvelope bbox = new CRSEnvelope();

            String crs = attrs.getValue("CRS"); //$NON-NLS-1$
            if (crs == null || crs.length() == 0) {
                crs = attrs.getValue("crs"); //$NON-NLS-1$
            }
            if (crs == null || crs.length() == 0) {
                crs = attrs.getValue("SRS"); //$NON-NLS-1$
            }
            if (crs == null || crs.length() == 0) {
                crs = attrs.getValue("srs"); //$NON-NLS-1$
            }

            if (crs == null || crs.length() == 0) {
                throw new SAXException("Bounding Box element contains no CRS/SRS attribute"); //$NON-NLS-1$
            }

            bbox.setEPSGCode(crs.toUpperCase());
            bbox.setMinX(Double.parseDouble(attrs.getValue("minx"))); //$NON-NLS-1$
            bbox.setMaxX(Double.parseDouble(attrs.getValue("maxx"))); //$NON-NLS-1$
            bbox.setMinY(Double.parseDouble(attrs.getValue("miny"))); //$NON-NLS-1$
            bbox.setMaxY(Double.parseDouble(attrs.getValue("maxy"))); //$NON-NLS-1$

            return bbox;
        }

        @Override
        public String getName() {
            return "BoundingBox"; //$NON-NLS-1$
        }

        @Override
        public Class<?> getInstanceType() {
            return CRSEnvelope.class;
        }

        @Override
        public boolean canEncode(Element element, Object value, Map hints) {
            return false;
        }

        @Override
        public void encode(Element element, Object value, PrintHandler output, Map hints)
                throws IOException, OperationNotSupportedException {
            throw new OperationNotSupportedException();
        }
    }

    public static class _TileSetType extends WMSCComplexType {
        private static final WMSCComplexType instance = new _TileSetType();

        private static Element[] elems = new Element[] {

                new WMSCElement("SRS", XSISimpleTypes.String.getInstance()), //$NON-NLS-1$
                new WMSCElement("BoundingBox", _BoundingBoxType.getInstance()), //$NON-NLS-1$
                new WMSCElement("Resolutions", XSISimpleTypes.String.getInstance()), //$NON-NLS-1$
                new WMSCElement("Width", XSISimpleTypes.Integer.getInstance()), //$NON-NLS-1$
                new WMSCElement("Height", XSISimpleTypes.Integer.getInstance()), //$NON-NLS-1$
                new WMSCElement("Format", XSISimpleTypes.String.getInstance()), //$NON-NLS-1$
                new WMSCElement("Layers", XSISimpleTypes.String.getInstance()), //$NON-NLS-1$
                new WMSCElement("Styles", XSISimpleTypes.String.getInstance()) //$NON-NLS-1$
        };

        private static Sequence seq = new SequenceGT(elems);

        public static WMSCComplexType getInstance() {
            return instance;
        }

        @Override
        public Attribute[] getAttributes() {
            return null;
        }

        @Override
        public ElementGrouping getChild() {
            return seq;
        }

        @Override
        public Element[] getChildElements() {
            return elems;
        }

        @Override
        public Object getValue(Element element, ElementValue[] value, Attributes attrs, Map hints)
                throws SAXException, OperationNotSupportedException {

            TileSet tileset = new WMSTileSet();

            for (int i = 0; i < value.length; i++) {

                if (sameName(elems[0], value[i])) {
                    String srs = (String) value[i].getValue();
                    tileset.setCoorindateReferenceSystem(srs);
                }

                if (sameName(elems[1], value[i])) {
                    CRSEnvelope bbox = (CRSEnvelope) value[i].getValue();
                    tileset.setBoundingBox(bbox);
                }

                if (sameName(elems[2], value[i])) {
                    String resolutions = (String) value[i].getValue();
                    tileset.setResolutions(resolutions);
                }

                if (sameName(elems[3], value[i])) {
                    Integer width = (Integer) value[i].getValue();
                    tileset.setWidth(width);
                }

                if (sameName(elems[4], value[i])) {
                    Integer height = (Integer) value[i].getValue();
                    tileset.setHeight(height);
                }

                if (sameName(elems[5], value[i])) {
                    String format = (String) value[i].getValue();
                    tileset.setFormat(format);
                }

                if (sameName(elems[6], value[i])) {
                    String layers = (String) value[i].getValue();
                    tileset.setLayers(layers);
                }

                if (sameName(elems[7], value[i])) {
                    String styles = (String) value[i].getValue();
                    tileset.setStyles(styles);
                }
            }
            if (tileset.getCoordinateReferenceSystem() == null) {
                // we are unable to use this one; we do not support
                // this projection
                return null;
            }
            return tileset;
        }

        @Override
        public String getName() {
            return "WMSTileSet"; //$NON-NLS-1$
        }

        @Override
        public Class<?> getInstanceType() {
            return WMSTileSet.class;
        }

        @Override
        public boolean canEncode(Element element, Object value, Map hints) {
            return false;
        }

        @Override
        public void encode(Element element, Object value, PrintHandler output, Map hints)
                throws IOException, OperationNotSupportedException {
            throw new OperationNotSupportedException();
        }
    }

    public static class _VendorSpecificCapabilitiesType extends WMSCComplexType {
        private static final WMSCComplexType instance = new _VendorSpecificCapabilitiesType();

        private static Element[] elems = new Element[] {
                new WMSCElement("TileSet", _TileSetType.getInstance()) //$NON-NLS-1$
        };

        private static Sequence seq = new SequenceGT(elems);

        public static WMSCComplexType getInstance() {
            return instance;
        }

        @Override
        public Attribute[] getAttributes() {
            return null;
        }

        @Override
        public ElementGrouping getChild() {
            return seq;
        }

        @Override
        public Element[] getChildElements() {
            return elems;
        }

        @Override
        public Object getValue(Element element, ElementValue[] value, Attributes attrs, Map hints)
                throws SAXException, OperationNotSupportedException {

            VendorSpecificCapabilities capabilities = new VendorSpecificCapabilities();

            for (int i = 0; i < value.length; i++) {
                if (sameName(elems[0], value[i])) {
                    WMSTileSet tile = (WMSTileSet) value[i].getValue();
                    if (tile != null) {
                        capabilities.addTile(tile);
                    }
                }
            }
            return capabilities;
        }

        @Override
        public String getName() {
            return "VendorSpecificCapabilities"; //$NON-NLS-1$
        }

        @Override
        public Class<?> getInstanceType() {
            return VendorSpecificCapabilities.class;
        }

        @Override
        public boolean canEncode(Element element, Object value, Map hints) {
            return false;
        }

        @Override
        public void encode(Element element, Object value, PrintHandler output, Map hints)
                throws IOException, OperationNotSupportedException {
            throw new OperationNotSupportedException();
        }
    }

    public static class _WMT_MS_CapabilitiesType extends WMSCComplexType {
        private static final WMSCComplexType instance = new _WMT_MS_CapabilitiesType();

        /**
         * The vendor specific capabilities in this list should not be necessary once GeoWebCache
         * returns a valid WMS document. This is currently in there to support the GeoWebCache
         * current getCapabilities document
         */
        private static Element[] elems = new Element[] {
                new WMSCElement("Service", _ServiceType.getInstance()), //$NON-NLS-1$
                new WMSCElement("Capability", _CapabilityType.getInstance()), //$NON-NLS-1$
                new WMSCElement("VendorSpecificCapabilities", //$NON-NLS-1$
                        _VendorSpecificCapabilitiesType.getInstance()) };

        private static Sequence seq = new SequenceGT(elems);

        private static Attribute[] attrs = new Attribute[] {
                new WMSCAttribute(null, "version", WMSCSchema.NAMESPACE, //$NON-NLS-1$
                        XSISimpleTypes.String.getInstance(), Attribute.REQUIRED, null, null, false),
                new WMSCAttribute("updateSequence", XSISimpleTypes.String.getInstance()) }; //$NON-NLS-1$

        public static WMSCComplexType getInstance() {
            return instance;
        }

        @Override
        public Attribute[] getAttributes() {
            return attrs;
        }

        @Override
        public ElementGrouping getChild() {
            return seq;
        }

        @Override
        public Element[] getChildElements() {
            return elems;
        }

        @Override
        public Object getValue(Element element, ElementValue[] value, Attributes attrs, Map hints)
                throws SAXException, OperationNotSupportedException {

            WMSCCapabilities capabilities = new WMSCCapabilities();

            for (int i = 0; i < value.length; i++) {
                ElementValue elementValue = value[i];
                // Service
                if (sameName(elems[0], elementValue)) {
                    Service x = ((Service) elementValue.getValue());
                    capabilities.setService(x);
                }
                // Capability
                if (sameName(elems[1], elementValue)) {
                    Capability c = ((Capability) elementValue.getValue());
                    capabilities.setCapabilitiy(c);
                }
                // VendorSpecificCapabilities
                if (sameName(elems[2], elementValue)) {
                    // vendor specific capabilities for 1.0alpha version of
                    // geowebcache; once geowebcache fixed up this should not be necessary as
                    // this should be inside a capabilities
                    Capability c = new Capability();
                    VendorSpecificCapabilities cs = ((VendorSpecificCapabilities) elementValue
                            .getValue());
                    c.setVendorCapabilities(cs);
                    capabilities.setCapabilitiy(c);
                }
            }

            capabilities.setVersion(attrs.getValue("", "version")); //$NON-NLS-1$ //$NON-NLS-2$
            capabilities.setUpdateSequence(attrs.getValue("", "updateSequence")); //$NON-NLS-1$//$NON-NLS-2$

            return capabilities;
        }

        @Override
        public String getName() {
            return "WMT_MS_Capabilities"; //$NON-NLS-1$
        }

        @Override
        public Class<?> getInstanceType() {
            return WMSCCapabilities.class;
        }

        @Override
        public boolean canEncode(Element element, Object value, Map hints) {
            return false;
        }

        @Override
        public void encode(Element element, Object value, PrintHandler output, Map hints)
                throws IOException, OperationNotSupportedException {
            throw new OperationNotSupportedException();
        }
    }

    public static class _CapabilityType extends WMSCComplexType {
        private static final WMSCComplexType instance = new _CapabilityType();

        private static Element[] elems = new Element[] { new WMSCElement(
                "VendorSpecificCapabilities", _VendorSpecificCapabilitiesType.getInstance()) //$NON-NLS-1$
        };

        private static Sequence seq = new SequenceGT(elems);

        private static Attribute[] attrs = new Attribute[] {};

        public static WMSCComplexType getInstance() {
            return instance;
        }

        @Override
        public Attribute[] getAttributes() {
            return attrs;
        }

        @Override
        public ElementGrouping getChild() {
            return seq;
        }

        @Override
        public Element[] getChildElements() {
            return elems;
        }

        @Override
        public Object getValue(Element element, ElementValue[] value, Attributes attrs, Map hints)
                throws SAXException, OperationNotSupportedException {

            Capability capabilities = new Capability();

            for (int i = 0; i < value.length; i++) {
                if (sameName(elems[0], value[i])) {
                    VendorSpecificCapabilities vc = ((VendorSpecificCapabilities) value[i]
                            .getValue());
                    capabilities.setVendorCapabilities(vc);
                }
            }
            return capabilities;
        }

        @Override
        public String getName() {
            return "Capability"; //$NON-NLS-1$
        }

        @Override
        public Class<?> getInstanceType() {
            return Capability.class;
        }

        @Override
        public boolean canEncode(Element element, Object value, Map hints) {
            return false;
        }

        @Override
        public void encode(Element element, Object value, PrintHandler output, Map hints)
                throws IOException, OperationNotSupportedException {
            throw new OperationNotSupportedException();
        }
    }

    /**
     * Represents a service complex type from WMSC. Currently this only parses out the Name, Title
     * and OnlineResource attributes.
     *
     * @author Emily Gouge (Refractions Research, Inc).
     * @since 1.1.0
     */
    public static class _ServiceType extends WMSCComplexType {
        private static final WMSCComplexType instance = new _ServiceType();

        private static Element[] elems = new Element[] {
                new WMSCElement("Name", XSISimpleTypes.String.getInstance()), //$NON-NLS-1$
                new WMSCElement("Title", XSISimpleTypes.String.getInstance()), //$NON-NLS-1$
                new WMSCElement("OnlineResource", XSISimpleTypes.String.getInstance()) //$NON-NLS-1$
        };

        private static Sequence seq = new SequenceGT(elems);

        public static WMSCComplexType getInstance() {
            return instance;
        }

        @Override
        public Attribute[] getAttributes() {
            return null;
        }

        @Override
        public ElementGrouping getChild() {
            return seq;
        }

        @Override
        public Element[] getChildElements() {
            return elems;
        }

        @Override
        public Object getValue(Element element, ElementValue[] value, Attributes attrs, Map hints)
                throws SAXException, OperationNotSupportedException {

            Service service = new Service();

            for (int i = 0; i < value.length; i++) {

                if (sameName(elems[0], value[i])) {
                    service.setName((String) value[i].getValue());
                }

                if (sameName(elems[1], value[i])) {
                    service.setTitle((String) value[i].getValue());
                }

                // OnlineResource
                if (sameName(elems[2], value[i])) {
                    String spec = (String) value[i].getValue();
                    if (spec == null || spec.length() == 0) {
                        // Service not available
                        System.out.println("OnlineResource cannot be empty"); //$NON-NLS-1$
                    } else {
                        try {
                            service.setOnlineResource(new URL(spec));
                        } catch (MalformedURLException e) {
                            System.out.println("OnlineResource cannot string to url: " + spec); //$NON-NLS-1$
                        }
                    }
                }

            }
            return service;
        }

        @Override
        public String getName() {
            return "Service"; //$NON-NLS-1$
        }

        @Override
        public Class<?> getInstanceType() {
            return Service.class;
        }

        @Override
        public boolean canEncode(Element element, Object value, Map hints) {
            return false;
        }

        @Override
        public void encode(Element element, Object value, PrintHandler output, Map hints)
                throws IOException, OperationNotSupportedException {
            throw new OperationNotSupportedException();
        }
    }

}
