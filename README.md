# Introduction

StructuredSchema is a language neutral specification for the validation of structured data values against a structured data schema.

# Structured data

Structured data is defined as data having the abstract structure defined in the JSON specification, with the exception that integers and decimals are considered separately. Thus it comprises of boolean, integer, decimal, string as scalar values, plus arrays (aka lists, sequences) and objects (mapping values to string keys) as compound values. There is also the null value.

Though JSON defines a text format, the underlying abstract data structure is general, and can be reified, displayed and transported in many formats (for example as YAML documents, javascript objects, protobuf bitpacking etc). Note, however, that formats often have capabilities beyond this structuring, and care should be taken to not exceed this abstract structure. 

A *structured value* (or simply *value*) is a piece of data following this structure, regardless of the medium in which it is realised.

# Type based validation

A *type* is understood as a set of structured values matching that type. A *structured schema* is the expression of a type as a structured value. A structured value can then be validated against such a schema, and passes only if it is a member of the set of values matching the type defined by the schema.

The result of validation can itself be structured data - as a list of validation failures. An empty list indicates that validation passed.

# Type literals

Corresponding to the base data types (Boolean, Integer, Decimal, String, Object, Array and Null), *type literals* define subsets of each data type.

For Boolean, Integer, Decimal, and Null types, type literals corresponding to a single value in those types may be formed as the value itself (preferred where possible) or as string. For example 52 or "52".

## Boolean

There are exactly two literals, 'true' and 'false', matching the Boolean values true and false. 

## Integer

Single value positive and negative integers, including exponention.

Integer intervals have the string form 'n..m' where n,m are integers and n<m. Values match when they fall into the range, ie. n<=x<=m. Either n or m, or both, can be left out to yield an open ended range. In particular, '..' covers all integers.

## Decimal

Single value positive and negative decimals, including exponention, in either string or decimal form, matching an exact decimal value (leading/trailing zeros are ignored). 

Decimal intervals have the string form 'n...m' where n,m are decimals and n<m. Values match when they fall into the range, ie. n<=x<=m. Either n or m, or both, can be left out to yield an open ended range. In particular, '...' covers all decimals. Note the extra dot to distinguish from integer intervals.

## String

As strings are treated as type expressions, special syntax is necessary to disambiguate.

A string type literal corresponding to a single string is of the form '\_abc\_' where abc is the (possibly empty) string to be defined. Any \_ in abc are escaped by doubling, eg: '\_hello\_\_world\_' => 'hello\_world'.

Regex type literals are of the form '/regex/' where regex is a valid regex expression, any string values matching the regex fall within that type literal. Any / in regex should be escaped by doubling, eg: '/\d?//\d?/' => '\d?/\d?'.

## Object

Defined in Object form, with a key corresponding to each key in a matching Object value. The key values are type definitions


Compare with the Object builtin named type.

## Array




## Null

Exactly one literal, 'null', matches only a null value.

## Wild

Exactly one literal, '*', matches any value, including null.

## Discriminator

# Type expressions

# Type union

Given two type expressions A and B, their type union has the form 'A|B'. A value matches if is matches either A or B. Type union is associative, so unions of more than two types can be expressed as A|B|C etc. Note that Object and Array type literals cannot be unioned with this operator.

# Named types

## Parameter passing and defaults

## Builtin named types, Object and Array

## Standard Library

## Extended types

## Discriminated unions 

# Errors

# Limitations

# Test Suite

# Implementations


