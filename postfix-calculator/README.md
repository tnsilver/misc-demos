# Postfix Calculator

As opposed to regular _infix_ arithmetic expressions such as (a + b), _postfix_ expressions (AKA: 'Reversed Polish Notation') are written operands first, operators later such as (a b +). Postfix calculators can evaluate _postfix_ expressions and calculate results. This is one of those academic calculator projects, but it (hopefully) brings a new level of configurability to the plain of postfix calculators.

This multi module parent project includes 3 core modules:

* [rpn.support](rpn.support/README.md) - API and core classes

* [infix.converter](infix.converter/README.md) - a utility to convert infix expressions to postfix

* [postfix.calculator](postfix.calculator/README.md) - the workhorse of this project, the postfix calculator.

This project also contains a usage examples project

* [calculator.example](calculator.example/README.md) - examples and extensive documentation demonstrating how to use the postfix calculator and configure it


To build the project and generate the documentation using _Maven_:

```
tom@localhost:/workspaces/rpn$ mvn clean
tom@localhost:/workspaces/rpn$ mvn package site
```

See the reports at _<your-path>/rpn/target/site/index.html_

To start using the calculator in your own code, you must first **install** it to a local maven repository:

```
tom@localhost:/workspaces/rpn$ mvn install
```

------------------------------------------------------------

## Features

* accepts both types of expressions: _infix_ (a + b) and _postfix_ (a b +) expressions


* fluent API: ```BigDecimal result = RPNCalculator.convert("sin(90) / 2").doPrint().thenCalculate();```


* supports nested functions: ```BigDecimal result = RPNCalculator.convert("avg(√2,[min(sqrt(π),max(3.1428,π)) + max(2*π,sin(90))])").thenCalculate();```


* supports new functions: ```context.registerFunction("rms", UNARY, arr -> arr[0].multiply(valueOf(0.7071068)));```


* supports new operators: ```context.registerOperator("r", HIGH, LEFT, UNARY, (arr) -> ONE.divide(arr[0], context.getPrecision(), context.getRoundingMode()));```


* supports new constants: ```context.registerConstant("inch", BigDecimal.valueOf(2.54));```


* supports variables: ```context.addVariable('~', 120D);```


* default unary operators: ! (factorial), √


* default binary operators: ^ (exponential), * and ×, / and ÷, % (modulus), +, − and -


* default unary functions: sin, cos, tan, log (base 10)


* default binary functions: min, max, avg, pct, sum


* default constants: π or PI, e (base of the natural logarithm)


* recognizes operators precedence and associativity and distinguishes unary and binary operators and functions


* designed on pure **POJO** none intrusive approach and does not employ external libraries other than SLF4J for logging


* M1 only supports unary and binary (2 arguments) functions such as _sum(4,10)_.


* M2 is planned to also support multi arguments functions such as _sum(4,10,12,23...)_


------------------------------------------------------------


## Getting Started

* Please see the [README.md](calculator.example/README.md) for advanced _RPNCalculator_ usage examples


* Once the main project is installed to the local maven repository, you can _convert_ an _infix_ expression to _postfix_ and evaluate it like this:

```
import java.math.BigDecimal;
import org.silvermania.rpn.postfix.calculator.RPNCalculator;

public class Main {

    public static void main(String[] args) {
        BigDecimal result = RPNCalculator.convert("22/7").thenCalculate(); // provide infix expression and calculate
        System.out.format("result: %s%n",result);
    }

}
```

* When you provide an _infix_ expression to the _postfix_ calculator using the _convert_ method, you implicitly invoke two extra stages. The first includes normalizing the input _infix_ expression to a space separated copy of the original expression. This is performed by the **InfixNormalizer** and it's required for the second stage, which parses the normalized _infix_ expression left to right and converts it into a normalized (space separated) _postfix_ expression. This is the job of the **InfixConverter**. The _postfix_ calculator does not directly process infix expressions in calculations, so the ability to provide one nonetheless requires some workarounds and trickery. The best input for a _postfix_ calculator is, well... a _postfix_ expression. This eliminates the two redundant normalization and conversion stages.


* **Note** however, an input _postfix_ expression must be space separated. Spaces are expected between operands, constants, operators and functions. For example, this nested functions _infix_ expression _"min(√4,max(4,8))"_ translates to a normalized _postfix_ expression of: _"4 √ 4 8 max min"_


* To provide a postfix expression to the postfix calculator use the _accept_ method:

```
import java.math.BigDecimal;
import org.silvermania.rpn.postfix.calculator.RPNCalculator;

public class Main {

    public static void main(String[] args) {
        BigDecimal result = RPNCalculator.accept("22 7 /").thenCalculate(); // provide postfix expression and calculate
        System.out.format("result: %s%n",result);
    }

}
```

* The RPN Calculator is highly configureable, so you can take it up a notch, add variables and register your own functions:


```
import static java.math.BigDecimal.valueOf;
import static org.silvermania.rpn.support.Multiplicity.UNARY;

import java.math.BigDecimal;

import org.silvermania.rpn.postfix.calculator.RPNCalculator;
import org.silvermania.rpn.support.CalculationContext;

public class Main {

    public static void main(String[] args) {
        CalculationContext context = CalculationContext.newInstance(); // obtain default calculation context
        context.addVariable("~", BigDecimal.valueOf(120));  // define AC line voltage variable
        context.registerFunction("rms", UNARY, arr -> arr[0].multiply(valueOf(0.7071068))); // define unary function 'rms'
        BigDecimal rmsVolts = RPNCalculator.withContext(context).convert("rms(~)").thenCalculate(); // provide context and infix (converts to "~ rms"), then calculate
        System.out.format("root mean square voltage: %s%n",rmsVolts);
    }

}
```

* Of course, this is a postfix calculator so if you're comfortable with postfix expressions you could provide it to the calculator directly and save the conversion:


```
import static java.math.BigDecimal.valueOf;
import static org.silvermania.rpn.support.Multiplicity.UNARY;

import java.math.BigDecimal;

import org.silvermania.rpn.postfix.calculator.RPNCalculator;
import org.silvermania.rpn.support.CalculationContext;

public class Main {

    public static void main(String[] args) {
        CalculationContext context = CalculationContext.newInstance(); // obtain default calculation context
        context.addVariable("~", BigDecimal.valueOf(120));  // define AC line voltage variable
        context.registerFunction("rms", UNARY, arr -> arr[0].multiply(valueOf(0.7071068))); // define unary function 'rms'
        BigDecimal rmsVolts = RPNCalculator.withContext(context).accept("~ rms").thenCalculate(); // provide context and postfix, then calculate
        System.out.format("root mean square voltage: %s%n",rmsVolts);
    }

}
```

------------------------------------------------------------

### Prerequisites

To run the software you will need to install:

* OpenJDK >= 12.0.1 (<https://jdk.java.net/12/>)
* Apache Maven >= 3.6.1 (<https://maven.apache.org/download.cgi>)

To modify, extend, develop this software you will need Eclipse IDE for Java development:

* Eclipse >= jee-2019-06-RC1 (<https://www.eclipse.org/downloads/packages/release/2019-06/rc1>)

------------------------------------------------------------

### Installing

After obtaining the software source code and downloading it to a local directory follow these steps:

* CD into the installation directory

```
tom@localhost:/workspaces$ cd rpn
```
* build the main rpn project, run the tests and install it to the local maven repository:

```
tom@localhost:/workspaces/rpn$ mvn install
```
* to build the main project and generate the project site and reports without installing it to the local maven repository:

```
tom@localhost:/workspaces/rpn$ mvn clean package
tom@localhost:/workspaces/rpn$ mvn site
```
* **Note** however, you cannot run the example project test cases without first installing the main rpn project to the local repository.


* Installing the rpn calculator main project will let you use it in your code so you could do, for example, this:

```
import java.math.BigDecimal;

import org.silvermania.rpn.postfix.calculator.RPNCalculator;

public class Main {

    public static void main(String[] args) {
        // assume a radius of 12 and we want the circle area...
        BigDecimal area = RPNCalculator.convert("π*12 ^ 2").thenCalculate(); // converts to postfix "π 12 * 2 ^" and calculates 1421.2230305
        System.out.format("area: %s%n",area);
    }
}
```

* or, if you are fluent in postfix syntax, you could simply input a postfix expression and save the conversion from infix:


```
import org.silvermania.rpn.postfix.calculator.RPNCalculator;

public class Main {

    public static void main(String[] args) {
        // assume a radius of 12 and we want the circle area...
        BigDecimal area = RPNCalculator.accept("π 12 * 2 ^").thenCalculate(); // calculates 1421.2230305
        System.out.format("area: %s%n",area);
    }
}
```

* you can even define your radius as a variable and use it further upstream:

```
import org.silvermania.rpn.postfix.calculator.RPNCalculator;
import org.silvermania.rpn.support.CalculationContext;

public class Main {

    public static void main(String[] args) {
        CalculationContext context = CalculationContext.newInstance();
        context.addVariable("r",12.0); // register a variable 'r' with Double value 12
        BigDecimal area = RPNCalculator.withContext(context).convert("π*r ^ 2").thenCalculate(); // converts to postfix "π r * 2 ^" and calculates 1421.2230305
        System.out.format("area: %s%n",area);
    }

}
```
------------------------------------------------------------

## Running the tests

* Any of the _mvn package_, _mvn install_, _mvn site_, _mvn test_ commands will run the unit tests in any of the sub modules. The recommended way to run the tests and generate the reports in case the main project **is already installed** in the local maven repository, is to run the following command from the root of the main project:

```
tom@localhost:/workspaces/rpn$ mvn clean site
```

* The recommended way to run the tests and generate the reports in case the main project **is NOT installed** in the local maven repository, is to run the following commands from the root of the main project:

```
tom@localhost:/workspaces/rpn$ mvn clean package
tom@localhost:/workspaces/rpn$ mvn site
```

* Reports are generated in the _rpn/target/site_ sub directory and are accessible via the standard _rpn/target/site/index.html_ page


* **Note** however, that if the main project is not installed in the local maven repository, you will not be able to build and run the tests in modules _infix.converter_ and _postfix.calculator_ because they depend on the API module _postfix.support_ which is not in the local repository. You will also not be able to build or test the example project _calculator.example_ under the main project's root directory.


* For running the tests in development mode, you could install only the required dependencies for each module you are working on. Instructions are in the _pom.xml_ file of each sub-module and are module specific, but the essence is that if you want to build the _infix.converter_ sub-module for example, which, in this case depends on the common API sub-module _postfix.support_, you will need, at the very minimum, to install it's dependencies as follows:

```
tom@localhost:/workspaces/rpn$ mvn install -pl rpn.support -am
```

### Break down into end to end tests


## module _rpn.support_ tests

* package _org.silvermania.rpn.support_ - tests the functionality of the common API classes that are used to break down a postfix expression into parseable segments and allows for registering operators, functions, variables and constants to be recognized in a given postfix expression.


## module _infix.converter_ tests

* package _org.silvermania.rpn.infix.support_ - tests the functionality of the two key players in handling infix conversion. The _InfixNormalizer_ and the _InfixTokenHandler_. The _InfixNormalizer_ is responsible for normalizing an infix expression into a space separated char sequence, before the expression can be converted to well formed postfix. The _InfixTokenHandler_ is the engine of the _InfixConverter_ which converts an infix expression to postfix. It does so by parsing the expression left to right and handing tokens to the _InfixTokenHandler_ for restructuring to postfix.


* package _org.silvermania.rpn.infix.converter_ - integration tests of the complete functionality of the _InfixConverter_ which accepts infix expressions and converts them to postfix


## module _postfix.calculator_ tests

* package _org.silvermania.rpn.postfix.support_ - tests the functionality of the modules classes that are designed to help parsing a postfix expression and configure the calculator and it's fluent API.


* package _org.silvermania.rpn.postfix.calculator_ - integration tests of the complete functionality of the _RPNCalculator_

## project _calculator.example_ tests

* package _org.silvermania.calculator.example_ - demonstrates via extensively documented test cases, the usage and fluent API of the _RPNCaclulator_. These test are not designed for testing (all functionality is tested in the sub-modules tests), but rather intended for users to generate the site and review the Javadoc explanations regarding each example.


## Generating Test Reports

* To run specific module tests. for example _rpn.support_, and generate the test reports at _rpn/rpn.support/target/site/surefire-report.html_:

```
tom@localhost:/workspaces/rpn$ cd rpn.support
tom@localhost:/workspaces/rpn/rpn.support/$mvn package site
```

### Coding style tests

* Please note the standard _mvn clean checkstyle:checkstyle_ command will not generate the expected checkstyle reports as this project uses a customized _checkstyle-rules.xml_ configuration file, different than the default _sun_checks.xml_ configurations, and it resides under the _src/main/resources_ maven directory of each module.


* The checkstyle reports are generated together with each module's site, or with the main project's site and will become available from the _<module>/target/site/index.html_ page


------------------------------------------------------------


## Built With

* [Eclipse IDE 4.12.0RC1](https://www.eclipse.org/downloads/packages/release/2019-06/rc1) - 2019-06 RC1 Tools for Java developers creating Enterprise Java and Web applications, including a Java IDE, tools for Enterprise Java, JPA, JSF, Mylyn, Maven, Git and more.
* [Apache Maven](https://maven.apache.org/download.cgi) - 3.6.1 is the latest release and recommended version for all users.
* [OpenJDK](https://jdk.java.net/12/) - 12.0.1 General-Availability Release
* [GNU/Linux](http://cdimage.ubuntu.com/kubuntu/releases/18.04/release/) - Kubuntu 18.04.2 LTS (bionic beaver)


------------------------------------------------------------


## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.


------------------------------------------------------------


## Versioning

* Version structure - _{number}.{release_type}_ where {number} is further broken down as {major}.{minor}.{micro}.


* Examples:

```
1.0.0.BUILD-SNAPSHOT
1.0.0.M1
1.0.0.M2
1.0.0.RC1
1.0.0.RC2
1.0.0.RELEASE
```

------------------------------------------------------------


## Credits

![T.N.Silverman](../.images/me.jpg?raw=true "T.N.Silverman")

* All java code, property files, Java Scripts, JSP's and HTML templates created by [T.N. Silverman](https://github.com/tnsilver "About T.N.Silverman")



* See also the list of [contributors](CONTRIBUTORS.md) who participated in this project.



* **Libraries:** This project uses the Simple Logging Facade for Java [SLF4J](https://www.slf4j.org/index.html) by **qos.ch** and their [LOGBACK](https://logback.qos.ch/) implementation.



* **Inspiration:** Algorithms and concepts: [Chris J.](https://www.chris-j.co.uk/parsing.php), and [Wikipedia - RPN](https://en.wikipedia.org/wiki/Reverse_Polish_notation)


------------------------------------------------------------


## License

[Licensed under the Apache License, Version 2.0](LICENSE "Apache License")


------------------------------------------------------------

