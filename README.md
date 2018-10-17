# Introduction

StructuredSchema is a language neutral specification for the validation of structured data values against a structured data schema.

# Structured data

Structured data will be defined here as data having the abstract structure defined in the JSON specification, with the exception that integers and decimals are considered separately. Thus it comprises of Boolean, Integer, Decimal, and String as scalar values, with Array (aka lists, sequences) and Object (mapping values to string keys) as compound values. There is also the Null type with its single value null.

For clarity, the nomenklatura of the JSON spec will be adopted thoughout this project, with the addition of the following terms:
field - the name/value pairs found in an object.
item - a value found in an array.

Though JSON defines a text format, the underlying abstract data structure is general, and can be reified, displayed and transported in many formats (for example as YAML documents, javascript objects, protobuf bitpacking etc). Note, however, that formats often have capabilities beyond this structuring, and care should be taken to not exceed this abstract structure. 

A *structured value* (or simply *value*) is a piece of data following this structure, regardless of the medium in which it is realised.

# Type based validation

A *type* is defined here as a set of structured values matching that type. A *structured schema* is the expression of a type as a structured value. A structured value can then be validated against such a schema, and passes only if it is a member of the set of values matching the type defined by the schema. The result of validation is itself structured data - a list of validation failures. An empty list indicates that validation passed.

The structured schema is an object with two fields - a *type definition* and (optionally) a *context*. The type definition is an object or array with type definitions as field or item values respectively, or a string denoting a *type expression*, as described below.

# Object type definition

Where a type definition is an object, then a matching value must also be an object with the same fields (except optional fields, see below). The value of each field in the matching object must match the type definition found at the corrsponding field value in the object type definition, and there shall be no additional fields. Where a field type definition is a string type expression, then the type expression may have a '?' appended to indicate the field is optional. Note that therefore field type definitions that are themselves objects, or arrays, cannot be optional - instead it is necessary to define them as named types, see below.

# Array type definition

Where a tyep definition is an array, the matching value must also be an array, of the same length, with each item value matching the type defintiion found at the coreesponding index in the array type defintion. This is appropriate for fixed length, possibly hetegeonous, arrays. For varible length honogenous arrays, the built in type constructor 'Array' is used, see below.


# Type expression

A *type expression* is a string that resolves to a type. It comprises of either a named type, optionally with type expression parameters, a type literal, or a union of two or more type expressions. The full BNF grammar is here. 

# Type literals

For the scalar types, ie not object or array, a *type literal* defines a subset of each data type.

## Boolean

There are exactly two literals, 'true' and 'false', matching the Boolean values true and false. 

## Integer

Single value positive and negative integers, including exponention.

Integer intervals have the string form 'n..m' where n,m are integers and n<m. Values match when they fall into the range, ie. n<=x<=m. Either n or m, or both, can be left out to yield an open ended range. In particular, '..' covers all integers.

## Decimal

Single value positive and negative decimals, including exponention, in either string or decimal form, matching an exact decimal value (leading/trailing zeros are ignored). 

Decimal intervals have the string form 'n...m' where n,m are decimals and n<m. Values match when they fall into the range, ie. n<=x<=m. Either n or m, or both, can be left out to yield an open ended range. In particular, '...' covers all decimals. Note the extra dot to distinguish from integer intervals.

## String

A string type literal corresponding to a single string is of the form '\_abc\_' where abc is the (possibly empty) string to be defined. Any \_ in abc are escaped by doubling, eg: '\_hello\_\_world\_' => 'hello\_world'.

Regex type literals are of the form '/regex/' where regex is a valid regex expression, any string values matching the regex fall within that type literal. Any / in regex should be escaped by doubling, eg: '/\d?//\d?/' => '\d?/\d?'.

## Null

Exactly one literal, 'null', matches only a null value.

## Wild

Exactly one literal, '*', matches anything: Object, Array or scalar, including null.

# Type union

Given two type expressions A and B, their type union has the form 'A|B'. A value matches if is matches either A or B. Type union is associative, so unions of more than two types can be expressed as A|B|C etc. Note that Object and Array type literals cannot be unioned with this operator.

# Named types and the context

## Parameter passing and defaults

Named types can be declared with parameters, and can be invoked with tyep expression parameters.  

## Builtin named types, Object and Array

## Extended types

## Discriminated unions 

## Standard Library

# Errors

# Limitations

# Test Suite

# Implementations


