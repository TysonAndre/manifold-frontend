package org.whdl.frontend.syntaxtree;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class TestScope {

  /*
   * Most of these tests depend on the correct operation of Variable. Look to
   * those tests if errors show up.
   */

  @Test
  public void testGetParentScope() {
    Scope foo = new Scope(null);
    Scope bar = new Scope(foo);
    assertSame(foo, bar.getParentScope());
  }

  private NamespaceIdentifier getNamespaceIdentifier() {
    return new NamespaceIdentifier("whdl:is:cool");
  }

  private VariableIdentifier getVariableIdentifier() {
    VariableIdentifier id = new VariableIdentifier(getNamespaceIdentifier(),
        "foo");

    return id;
  }

  private Expression getTypeExpression() {
    return new LiteralExpression(BitTypeValue.getInstance());
  }

  // FIXME(lucas) return an instance of the type
  private Expression getValueExpression() {
    return new LiteralExpression(BitValue.getInstance(true));
  }

  @Test
  public void testDefineVariable() throws MultipleDefinitionException {
    Scope s = new Scope();
    s.defineVariable(getVariableIdentifier(), getTypeExpression());
  }

  @Test(expected = MultipleDefinitionException.class)
  public void testDefineVariableMultiple() throws MultipleDefinitionException {
    Scope s = new Scope();
    s.defineVariable(getVariableIdentifier(), getTypeExpression());
    s.defineVariable(getVariableIdentifier(), getTypeExpression());
  }

  @Test
  public void testContainsIdentifier() throws MultipleDefinitionException {
    Scope s = new Scope();
    s.defineVariable(getVariableIdentifier(), getTypeExpression());
    assertTrue(s.containsIdentifier(getVariableIdentifier()));
  }

  @Test
  public void testGetVariableType() throws MultipleDefinitionException,
      TypeMismatchException, VariableNotDefinedException {

    Scope s = new Scope();
    s.defineVariable(getVariableIdentifier(), getTypeExpression());
    assertEquals(getTypeExpression().evaluate(),
        s.getVariableType(getVariableIdentifier()));
  }

  @Test(expected = VariableNotDefinedException.class)
  public void testGetVariableNotDefined() throws VariableNotDefinedException {
    Scope s = new Scope();
    s.getVariable(getVariableIdentifier());
  }

  @Test
  public void testGetVariable() throws MultipleDefinitionException,
      VariableNotDefinedException, TypeMismatchException {
    Scope s = new Scope();
    s.defineVariable(getVariableIdentifier(), getTypeExpression());
    Variable v = s.getVariable(getVariableIdentifier());

    assertEquals(v.getIdentifier(), getVariableIdentifier());
    assertEquals(v.getType(), getTypeExpression().evaluate());
  }

  @Test
  public void testGetVariable_ParentScope() throws MultipleDefinitionException {
    Scope s1 = new Scope();
    Scope s2 = new Scope(s1);
    s1.defineVariable(getVariableIdentifier(), getTypeExpression());
    try {
      Variable v = s2.getVariable(getVariableIdentifier());
    } catch (VariableNotDefinedException vnde) {
      fail("could not get a variable defined in a parent scope");
    }
  }

  @Test
  public void testGetVariableValue() throws MultipleDefinitionException,
      VariableNotDefinedException, MultipleAssignmentException,
      VariableNotAssignedException {

    Scope s = new Scope();
    s.defineVariable(getVariableIdentifier(), getTypeExpression());
    s.assignVariable(getVariableIdentifier(), getValueExpression());

    assertEquals(getValueExpression().evaluate(),
        s.getVariableValue(getVariableIdentifier()));
  }

  @Test(expected = VariableNotDefinedException.class)
  public void testAssignVariableNotDefined()
      throws VariableNotDefinedException, MultipleAssignmentException {
    Scope s = new Scope();
    s.assignVariable(getVariableIdentifier(), getValueExpression());
  }

  @Test
  public void testAssignVariable() throws MultipleDefinitionException,
      VariableNotDefinedException, MultipleAssignmentException,
      VariableNotAssignedException {
    Scope s = new Scope();
    s.defineVariable(getVariableIdentifier(), getTypeExpression());
    s.assignVariable(getVariableIdentifier(), getValueExpression());
    assertEquals(s.getVariableValue(getVariableIdentifier()),
        getValueExpression().evaluate());
  }

  @Test(expected = MultipleAssignmentException.class)
  public void testAssignVariableMultiple() throws MultipleDefinitionException,
      VariableNotDefinedException, MultipleAssignmentException {
    Scope s = new Scope();
    s.defineVariable(getVariableIdentifier(), getTypeExpression());
    s.assignVariable(getVariableIdentifier(), getValueExpression());
    s.assignVariable(getVariableIdentifier(), getValueExpression());
  }

}
