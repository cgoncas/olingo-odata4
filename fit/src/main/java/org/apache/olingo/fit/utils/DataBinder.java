/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.fit.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.Value;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.data.AtomEntityImpl;
import org.apache.olingo.commons.core.data.AtomEntitySetImpl;
import org.apache.olingo.commons.core.data.AtomPropertyImpl;
import org.apache.olingo.commons.core.data.CollectionValueImpl;
import org.apache.olingo.commons.core.data.ComplexValueImpl;
import org.apache.olingo.commons.core.data.JSONEntityImpl;
import org.apache.olingo.commons.core.data.JSONEntitySetImpl;
import org.apache.olingo.commons.core.data.JSONPropertyImpl;
import org.apache.olingo.commons.core.data.LinkImpl;
import org.apache.olingo.fit.metadata.EntityType;
import org.apache.olingo.fit.metadata.Metadata;
import org.apache.olingo.fit.metadata.NavigationProperty;
import org.springframework.beans.BeanUtils;

public class DataBinder {

  private final ODataServiceVersion version;

  public DataBinder(final ODataServiceVersion version) {
    this.version = version;
  }

  public JSONEntitySetImpl toJSONEntitySet(final AtomEntitySetImpl atomEntitySet) {
    final JSONEntitySetImpl jsonEntitySet = new JSONEntitySetImpl();

    BeanUtils.copyProperties(atomEntitySet, jsonEntitySet, "baseURI", "metadataContextURL");
    jsonEntitySet.setBaseURI(atomEntitySet.getBaseURI() == null
            ? null
            : atomEntitySet.getBaseURI().toASCIIString() + "/$metadata");

    final Collection<Entity> entries = jsonEntitySet.getEntities();
    for (Entity entity : atomEntitySet.getEntities()) {
      entries.add(toJSONEntityType((AtomEntityImpl) entity));
    }

    return jsonEntitySet;
  }

  public AtomEntitySetImpl toAtomEntitySet(final JSONEntitySetImpl jsonEntitySet) {
    final AtomEntitySetImpl atomEntitySet = new AtomEntitySetImpl();

    BeanUtils.copyProperties(jsonEntitySet, atomEntitySet, "baseURI", "metadataContextURL");
    atomEntitySet.setBaseURI(jsonEntitySet.getBaseURI() == null
            ? null
            : jsonEntitySet.getBaseURI().toASCIIString() + "/$metadata");

    final Collection<Entity> entries = atomEntitySet.getEntities();
    for (Entity entity : jsonEntitySet.getEntities()) {
      entries.add(toAtomEntity((JSONEntityImpl) entity));
    }

    return atomEntitySet;
  }

  public JSONEntityImpl toJSONEntityType(final AtomEntityImpl atomEntity) {
    final JSONEntityImpl jsonEntity = new JSONEntityImpl();

    BeanUtils.copyProperties(atomEntity, jsonEntity, "baseURI", "properties", "links");
    jsonEntity.setBaseURI(atomEntity.getBaseURI() == null ? null : atomEntity.getBaseURI().toASCIIString());
    jsonEntity.getOperations().addAll(atomEntity.getOperations());

    for (Link link : atomEntity.getNavigationLinks()) {
      final Link jlink = new LinkImpl();
      jlink.setHref(link.getHref());
      jlink.setTitle(link.getTitle());
      jlink.setType(link.getType());
      jlink.setRel(link.getRel());

      if (link.getInlineEntity() instanceof AtomEntityImpl) {
        final Entity inlineEntity = link.getInlineEntity();
        if (inlineEntity instanceof AtomEntityImpl) {
          jlink.setInlineEntity(toJSONEntityType((AtomEntityImpl) link.getInlineEntity()));
        }
      } else if (link.getInlineEntitySet() instanceof AtomEntitySetImpl) {
        final EntitySet inlineEntitySet = link.getInlineEntitySet();
        if (inlineEntitySet instanceof AtomEntitySetImpl) {
          jlink.setInlineEntitySet(toJSONEntitySet((AtomEntitySetImpl) link.getInlineEntitySet()));
        }
      }

      jsonEntity.getNavigationLinks().add(jlink);
    }

    final Collection<Property> properties = jsonEntity.getProperties();
    for (Property property : atomEntity.getProperties()) {
      properties.add(toJSONProperty((AtomPropertyImpl) property));
    }

    return jsonEntity;
  }

  public AtomEntityImpl toAtomEntity(final JSONEntityImpl jsonEntity) {
    final AtomEntityImpl atomEntity = new AtomEntityImpl();

    final Metadata metadata = Commons.getMetadata(version);

    BeanUtils.copyProperties(jsonEntity, atomEntity, "baseURI", "properties", "links");
    atomEntity.setBaseURI(jsonEntity.getBaseURI() == null ? null : jsonEntity.getBaseURI().toASCIIString());

    for (Link link : jsonEntity.getNavigationLinks()) {
      final Link alink = new LinkImpl();
      alink.setHref(link.getHref());
      alink.setTitle(link.getTitle());

      final NavigationProperty navPropDetails =
              metadata.getEntityType(jsonEntity.getType()).getNavigationProperty(link.getTitle());

      alink.setType(navPropDetails != null && navPropDetails.isEntitySet()
              ? Constants.get(ConstantKey.ATOM_LINK_FEED) : Constants.get(ConstantKey.ATOM_LINK_ENTRY));
      alink.setRel(link.getRel());

      if (link.getInlineEntity() instanceof JSONEntityImpl) {
        final Entity inlineEntity = link.getInlineEntity();
        if (inlineEntity instanceof JSONEntityImpl) {
          alink.setInlineEntity(toAtomEntity((JSONEntityImpl) link.getInlineEntity()));
        }
      } else if (link.getInlineEntitySet() instanceof JSONEntitySetImpl) {
        final EntitySet inlineEntitySet = link.getInlineEntitySet();
        if (inlineEntitySet instanceof JSONEntitySetImpl) {
          alink.setInlineEntitySet(toAtomEntitySet((JSONEntitySetImpl) link.getInlineEntitySet()));
        }
      }

      atomEntity.getNavigationLinks().add(alink);
    }

    final EntityType entityType = StringUtils.isBlank(jsonEntity.getType())
            ? null : metadata.getEntityType(jsonEntity.getType());
    final Map<String, NavigationProperty> navProperties = entityType == null
            ? Collections.<String, NavigationProperty>emptyMap() : entityType.getNavigationPropertyMap();

    final List<Property> properties = atomEntity.getProperties();

    for (Property property : jsonEntity.getProperties()) {
      if (navProperties.containsKey(property.getName())) {
        final Link alink = new LinkImpl();
        alink.setTitle(property.getName());

        alink.setType(navProperties.get(property.getName()).isEntitySet()
                ? Constants.get(version, ConstantKey.ATOM_LINK_FEED)
                : Constants.get(version, ConstantKey.ATOM_LINK_ENTRY));

        alink.setRel(Constants.get(version, ConstantKey.ATOM_LINK_REL) + property.getName());

        if (property.getValue().isComplex()) {
          final Entity inline = new AtomEntityImpl();
          inline.setType(navProperties.get(property.getName()).getType());
          for (Property prop : property.getValue().asComplex().get()) {
            inline.getProperties().add(prop);
          }
          alink.setInlineEntity(inline);

        } else if (property.getValue().isCollection()) {
          final EntitySet inline = new AtomEntitySetImpl();
          for (Value value : property.getValue().asCollection().get()) {
            final Entity inlineEntity = new AtomEntityImpl();
            inlineEntity.setType(navProperties.get(property.getName()).getType());
            for (Property prop : value.asComplex().get()) {
              inlineEntity.getProperties().add(toAtomProperty((JSONPropertyImpl) prop, inlineEntity.getType()));
            }
            inline.getEntities().add(inlineEntity);
          }
          alink.setInlineEntitySet(inline);
        } else {
          throw new IllegalStateException("Invalid navigation property " + property);
        }
        atomEntity.getNavigationLinks().add(alink);
      } else {
        properties.add(toAtomProperty((JSONPropertyImpl) property, atomEntity.getType()));
      }
    }

    return atomEntity;
  }

  public JSONPropertyImpl toJSONProperty(final AtomPropertyImpl atomproperty) {
    final JSONPropertyImpl jsonproperty = new JSONPropertyImpl();
    BeanUtils.copyProperties(atomproperty, jsonproperty, "value");

    if (atomproperty.getValue().isComplex()) {
      final ComplexValueImpl complex = new ComplexValueImpl();
      jsonproperty.setValue(complex);

      for (Property field : atomproperty.getValue().asComplex().get()) {
        complex.get().add(toJSONProperty((AtomPropertyImpl) field));
      }
    } else if (atomproperty.getValue().isCollection()) {
      final CollectionValueImpl collection = new CollectionValueImpl();
      jsonproperty.setValue(collection);

      for (Value element : atomproperty.getValue().asCollection().get()) {
        if (element.isComplex()) {
          final ComplexValueImpl complex = new ComplexValueImpl();
          collection.get().add(complex);

          for (Property field : element.asComplex().get()) {
            complex.get().add(toJSONProperty((AtomPropertyImpl) field));
          }
        } else {
          collection.get().add(element);
        }
      }
    } else {
      jsonproperty.setValue(atomproperty.getValue());
    }

    return jsonproperty;
  }

  public AtomPropertyImpl toAtomProperty(final JSONPropertyImpl jsonproperty, final String entryType) {
    final AtomPropertyImpl atomproperty = new AtomPropertyImpl();
    atomproperty.setName(jsonproperty.getName());

    if (StringUtils.isNotBlank(jsonproperty.getType())) {
      atomproperty.setType(jsonproperty.getType());
    } else {
      final EntityType entityType = entryType == null ? null : Commons.getMetadata(version).getEntityType(entryType);
      if (entityType != null) {
        atomproperty.setType(entityType.getProperty(jsonproperty.getName()).getType());
      }
    }

    if (jsonproperty.getValue().isComplex()) {
      final ComplexValueImpl complex = new ComplexValueImpl();
      atomproperty.setValue(complex);

      for (Property field : jsonproperty.getValue().asComplex().get()) {
        complex.get().add(toAtomProperty((JSONPropertyImpl) field, atomproperty.getType()));
      }
    } else if (jsonproperty.getValue().isCollection()) {
      final CollectionValueImpl collection = new CollectionValueImpl();
      atomproperty.setValue(collection);

      for (Value element : jsonproperty.getValue().asCollection().get()) {
        if (element instanceof ComplexValueImpl) {
          final ComplexValueImpl complex = new ComplexValueImpl();
          collection.get().add(complex);

          for (Property field : element.asComplex().get()) {
            complex.get().add(toAtomProperty((JSONPropertyImpl) field,
                    atomproperty.getType() == null
                    ? null
                    : atomproperty.getType().replaceAll("^Collection\\(", "").replaceAll("\\)$", "")));
          }
        } else {
          collection.get().add(element);
        }
      }
    } else {
      atomproperty.setValue(jsonproperty.getValue());
    }

    return atomproperty;
  }
}