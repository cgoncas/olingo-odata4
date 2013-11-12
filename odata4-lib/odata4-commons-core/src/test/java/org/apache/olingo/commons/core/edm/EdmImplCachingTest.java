package org.apache.olingo.commons.core.edm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmServiceMetadata;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.junit.Before;
import org.junit.Test;

public class EdmImplCachingTest {

  private final FullQualifiedName NAME1 = new FullQualifiedName("testNamespace1", "testName1");
  private final FullQualifiedName NAME2 = new FullQualifiedName("testNamespace2", "testName2");
  private Edm edm;

  @Test
  public void cacheEntityContainer() {
    EdmEntityContainer entityContainer = edm.getEntityContainer(null);
    assertNotNull(entityContainer);

    EdmEntityContainer cachedContainer = edm.getEntityContainer(NAME1);
    assertNotNull(entityContainer);

    assertTrue(entityContainer == cachedContainer);
    assertEquals(entityContainer, cachedContainer);

    cachedContainer = edm.getEntityContainer(NAME1);
    assertNotNull(cachedContainer);

    assertTrue(entityContainer == cachedContainer);
    assertEquals(entityContainer, cachedContainer);

    EdmEntityContainer entityContainer2 = edm.getEntityContainer(NAME2);
    assertNotNull(entityContainer2);

    assertNotSame(entityContainer, entityContainer2);
  }

  @Test
  public void cacheEnumType() {
    EdmEnumType enumType = edm.getEnumType(NAME1);
    assertNotNull(enumType);

    EdmEnumType cachedType = edm.getEnumType(NAME1);
    assertNotNull(cachedType);

    assertTrue(enumType == cachedType);
    assertEquals(enumType, cachedType);

    EdmEnumType enumType2 = edm.getEnumType(NAME2);
    assertNotNull(enumType2);

    assertNotSame(enumType, enumType2);
  }

  @Test
  public void cacheTypeDefinition() {
    EdmTypeDefinition typeDefinition = edm.getTypeDefinition(NAME1);
    assertNotNull(typeDefinition);

    EdmTypeDefinition cachedDefinition = edm.getTypeDefinition(NAME1);
    assertNotNull(cachedDefinition);

    assertTrue(typeDefinition == cachedDefinition);
    assertEquals(typeDefinition, cachedDefinition);

    EdmTypeDefinition typeDefinition2 = edm.getTypeDefinition(NAME2);
    assertNotNull(typeDefinition2);

    assertNotSame(typeDefinition, typeDefinition2);
  }

  @Test
  public void cacheEntityType() {
    EdmEntityType entityType = edm.getEntityType(NAME1);
    assertNotNull(entityType);

    EdmEntityType cachedType = edm.getEntityType(NAME1);
    assertNotNull(cachedType);

    assertTrue(entityType == cachedType);
    assertEquals(entityType, cachedType);

    EdmEntityType entityType2 = edm.getEntityType(NAME2);
    assertNotNull(entityType2);

    assertNotSame(entityType, entityType2);
  }

  @Test
  public void cacheComplexType() {
    EdmComplexType complexType = edm.getComplexType(NAME1);
    assertNotNull(complexType);

    EdmComplexType cachedType = edm.getComplexType(NAME1);
    assertNotNull(cachedType);

    assertTrue(complexType == cachedType);
    assertEquals(complexType, cachedType);

    EdmComplexType complexType2 = edm.getComplexType(NAME2);
    assertNotNull(complexType2);

    assertNotSame(complexType, complexType2);
  }

  @Test
  public void cacheActionSimple() {
    EdmAction action = edm.getAction(NAME1, null, null);
    assertNotNull(action);

    EdmAction cachedAction = edm.getAction(NAME1, null, null);
    assertNotNull(cachedAction);

    assertTrue(action == cachedAction);
    assertEquals(action, cachedAction);

    EdmAction action2 = edm.getAction(NAME2, null, false);
    assertNotNull(action2);
    assertNotSame(action, action2);
  }

  @Test
  public void cacheActionComlex() {
    EdmAction action = edm.getAction(NAME1, null, null);
    assertNotNull(action);

    EdmAction cachedAction = edm.getAction(NAME1, null, true);
    assertNull(cachedAction);

    cachedAction = edm.getAction(NAME1, null, false);
    assertNull(cachedAction);

    cachedAction = edm.getAction(NAME1, NAME2, null);
    assertNull(cachedAction);

    cachedAction = edm.getAction(NAME1, NAME2, true);
    assertNull(cachedAction);

    cachedAction = edm.getAction(NAME1, NAME2, false);
    assertNull(cachedAction);

    cachedAction = edm.getAction(NAME1, NAME1, null);
    assertNull(cachedAction);

    cachedAction = edm.getAction(NAME1, NAME1, true);
    assertNull(cachedAction);

    cachedAction = edm.getAction(NAME1, NAME1, false);
    assertNull(cachedAction);
  }

  @Test
  public void cacheFunctionSimple() {
    EdmFunction function = edm.getFunction(NAME1, null, null, null);
    assertNotNull(function);

    EdmFunction cachedfunction = edm.getFunction(NAME1, null, null, null);
    assertNotNull(cachedfunction);

    assertTrue(function == cachedfunction);
    assertEquals(function, cachedfunction);

    EdmFunction function2 = edm.getFunction(NAME2, null, false, null);
    assertNotNull(function2);

    assertNotSame(function, function2);
  }

  @Test
  public void cacheFunctionComplex() {
    EdmFunction function = edm.getFunction(NAME1, null, null, null);
    assertNotNull(function);

    EdmFunction cachedfunction = edm.getFunction(NAME1, null, false, null);
    assertNull(cachedfunction);

    cachedfunction = edm.getFunction(NAME1, null, true, null);
    assertNull(cachedfunction);

    cachedfunction = edm.getFunction(NAME1, null, new Boolean(true), null);
    assertNull(cachedfunction);

    cachedfunction = edm.getFunction(NAME1, NAME2, null, null);
    assertNull(cachedfunction);

    cachedfunction = edm.getFunction(NAME1, NAME2, true, null);
    assertNull(cachedfunction);

    cachedfunction = edm.getFunction(NAME1, NAME2, false, null);
    assertNull(cachedfunction);

    cachedfunction = edm.getFunction(NAME1, null, null, new ArrayList<String>());
    assertNull(cachedfunction);
  }

  @Test
  public void cacheFunctionComplexWithListContent() {

  }

  @Test
  public void cacheServiceMetadata() {
    EdmServiceMetadata serviceMetadata = edm.getServiceMetadata();
    EdmServiceMetadata cachedMetadata = edm.getServiceMetadata();

    assertTrue(serviceMetadata == cachedMetadata);
    assertEquals(serviceMetadata, cachedMetadata);
  }

  @Before
  public void setup() {
    edm = new LocalEdm();
  }

  private class LocalEdm extends EdmImpl {
    @Override
    public EdmEntityContainer createEntityContainer(final FullQualifiedName fqn) {
      if (NAME1.equals(fqn) || fqn == null) {
        EdmEntityContainer container = mock(EdmEntityContainer.class);
        when(container.getNamespace()).thenReturn(NAME1.getNamespace());
        when(container.getName()).thenReturn(NAME1.getName());
        return container;
      } else if (NAME2.equals(fqn)) {
        EdmEntityContainer container = mock(EdmEntityContainer.class);
        when(container.getNamespace()).thenReturn(fqn.getNamespace());
        when(container.getName()).thenReturn(fqn.getName());
        return container;
      }
      return null;
    }

    @Override
    public EdmEnumType createEnumType(final FullQualifiedName fqn) {
      if (NAME1.equals(fqn) || NAME2.equals(fqn)) {
        EdmEnumType enumType = mock(EdmEnumType.class);
        when(enumType.getNamespace()).thenReturn(fqn.getNamespace());
        when(enumType.getName()).thenReturn(fqn.getName());
        return enumType;
      }
      return null;
    }

    @Override
    public EdmTypeDefinition createTypeDefinition(final FullQualifiedName fqn) {
      if (NAME1.equals(fqn) || NAME2.equals(fqn)) {
        EdmTypeDefinition typeDefinition = mock(EdmTypeDefinition.class);
        when(typeDefinition.getNamespace()).thenReturn(fqn.getNamespace());
        when(typeDefinition.getName()).thenReturn(fqn.getName());
        return typeDefinition;
      }
      return null;
    }

    @Override
    public EdmEntityType createEntityType(final FullQualifiedName fqn) {
      if (NAME1.equals(fqn) || NAME2.equals(fqn)) {
        EdmEntityType entityType = mock(EdmEntityType.class);
        when(entityType.getNamespace()).thenReturn(fqn.getNamespace());
        when(entityType.getName()).thenReturn(fqn.getName());
        return entityType;
      }
      return null;
    }

    @Override
    public EdmComplexType createComplexType(final FullQualifiedName fqn) {
      if (NAME1.equals(fqn) || NAME2.equals(fqn)) {
        EdmComplexType complexType = mock(EdmComplexType.class);
        when(complexType.getNamespace()).thenReturn(fqn.getNamespace());
        when(complexType.getName()).thenReturn(fqn.getName());
        return complexType;
      }
      return null;
    }

    @Override
    public EdmAction createAction(final FullQualifiedName fqn, final FullQualifiedName bindingParameterTypeName,
        final Boolean isBindingParameterCollection) {
      if (NAME1.equals(fqn) && bindingParameterTypeName == null && isBindingParameterCollection == null) {
        EdmAction action = mock(EdmAction.class);
        when(action.getNamespace()).thenReturn(fqn.getNamespace());
        when(action.getName()).thenReturn(fqn.getName());
        return action;
      } else if (NAME2.equals(fqn)) {
        EdmAction action = mock(EdmAction.class);
        when(action.getNamespace()).thenReturn(fqn.getNamespace());
        when(action.getName()).thenReturn(fqn.getName());
        return action;
      }
      return null;
    }

    @Override
    public EdmFunction createFunction(final FullQualifiedName fqn, final FullQualifiedName bindingParameterTypeName,
        final Boolean isBindingParameterCollection, final List<String> bindingParameterNames) {
      if (NAME1.equals(fqn) && bindingParameterTypeName == null && isBindingParameterCollection == null
          && bindingParameterNames == null) {
        EdmFunction function = mock(EdmFunction.class);
        when(function.getNamespace()).thenReturn(fqn.getNamespace());
        when(function.getName()).thenReturn(fqn.getName());
        return function;
      } else if (NAME2.equals(fqn)) {
        EdmFunction function = mock(EdmFunction.class);
        when(function.getNamespace()).thenReturn(fqn.getNamespace());
        when(function.getName()).thenReturn(fqn.getName());
        return function;
      }
      return null;
    }

    @Override
    public EdmServiceMetadata createServiceMetadata() {
      return mock(EdmServiceMetadata.class);
    }
  }
}