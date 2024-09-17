https://github.com/kappsegla/maven-java-template
have ben used for this javalab3

Inspiration for how to structure the code in Warehouse comes from
"Using Records in a Real Use Case" at https://dev.java/learn/records/

The aim is immutability for public methods in Waerhouse,
using functional programming with Streams &
working with parameterized Tests & MethodSource.
And no mocking with Mockito.

In some cases it would have been both easier, more straight forward &
more efficient to create each task in javalab3 from one stream.
The final result derives from starting with the later tasks,
the aim of consistency & less repeating code. 
It has though been educative to work with the setup made now.
Further improvements could include handling exceptions, test those &
more edge cases. It would also be better if the test providers could be
refactored for more dry code in WarehouseTest.

The method getAllCategoriesWithProducts() is mandatory for javalab3.
In this application there are now categories with no products.
All products have the attribute category so there might be products
without a value for the category, but not the other way around. 
