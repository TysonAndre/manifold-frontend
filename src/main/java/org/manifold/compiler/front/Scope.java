package org.manifold.compiler.front;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.manifold.compiler.TypeValue;
import org.manifold.compiler.Value;

// TODO dead code

public class Scope {
  private final Scope parentScope;
  private final Map<VariableIdentifier, Variable> symbolTable;

  public Set<VariableIdentifier> getSymbolIdentifiers() {
    return symbolTable.keySet();
  }

  public Scope(Scope parentScope) {
    this.symbolTable = new HashMap<>();
    this.parentScope = parentScope;
  }

  public Scope() {
    this.symbolTable = new HashMap<>();
    this.parentScope = null;
  }

  public Scope getParentScope() {
    return parentScope;
  }

  // at the time a variable is defined, all we may know about it
  // is that a binding for that name "will exist"
  public void defineVariable(VariableIdentifier identifier)
      throws MultipleDefinitionException {
    if (symbolTable.containsKey(identifier)) {
      throw new MultipleDefinitionException(identifier);
    }
    Variable v = new Variable(this, identifier);
    symbolTable.put(identifier, v);
  }

  public boolean isVariableDefined(VariableIdentifier identifier) {
    if (symbolTable.containsKey(identifier)) {
      return true;
    } else if (parentScope == null) {
      return false;
    } else {
      return parentScope.isVariableDefined(identifier);
    }
  }

  public TypeValue getVariableType(VariableIdentifier identifier)
      throws TypeMismatchException, VariableNotDefinedException {
    return getVariable(identifier).getType();
  }

  public Variable getVariable(VariableIdentifier identifier)
      throws VariableNotDefinedException {
    // TODO this does not handle namespaces correctly
    Variable v = symbolTable.get(identifier);
    if (v == null) {
      // no such variable in this scope; check parent scope
      if (parentScope == null) {
        throw new VariableNotDefinedException(identifier);
      } else {
        return parentScope.getVariable(identifier);
      }
    } else {
      return v;
    }
  }

  public Value getVariableValue(VariableIdentifier identifier)
      throws VariableNotAssignedException, VariableNotDefinedException {
    return getVariable(identifier).getValue();
  }

}
