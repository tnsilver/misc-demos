# Using The Calculator

This project includes JUnit 5 test cases and extensive _Javadoc_ documentation explaining the usage of the _RPN Calculator_.


To build the project and generate the documentation using _Maven_, you must first install the wrapping **rpn** project to the local _Maven_ repository. Then:

```
tom@localhost:/workspaces/rpn$ cd calculator.example
tom@localhost:/workspaces/rpn/calculator.example$ mvn clean package site
```

See the reports at _<your-path>/rpn/calculator.example/target/site/index.html_

## Getting Started


###  **Simple INFIX Examples:**

------------------------------------------------------------

**Note:** infix expressions (which are regular arithmetic expressions such as _1 + 2_ or _1/2 + (3*4) / 3_, where operators are between operands and may, or may not be surrounded by parentheses and brackets) are normalized into a space separated expression, then converted to **postfix**. As long as the arithmetic expression contains registered constants, operators and functions, functions arguments separator comma and matched parentheses, square or curly brackets - spacing does not matter.

* **Example:** To evaluate **infix** arithmetic expressions:

```
String infix = "1 * 2";
BigDecimal result = RPNCalculator.convert(infix).thenCalculate();
```

* This will produce a result of _2.0000000_


* **Example:** To evaluate **infix** arithmetic expressions **and** print an _INFO_ logging level console message with the detailed calculation parameters:

```
String infix = "1*2+(4/2)";
BigDecimal result = RPNCalculator.convert(infix).doPrint().thenCalculate();
```
* This will produce a result of _4.0000000_ and the console _INFO_ logging level printed report will look something like this:

```
CALCULATION PARAMETERS
----------------------
CONTEXT
  class             org.silvermania.rpn.support.CalculationContext
  mathContext       DECIMAL32 precision=7 roundingMode=HALF_EVEN
  constants         e,PI,π
  operators         ^,!,√,*,×,/,÷,%,mod,+,−,-
  funtions          sin,cos,tan,sqrt,min,max,avg,pct,pow,sum
CALCULATOR
  class             org.silvermania.rpn.postfix.calculator.RPNCalculator
  context           default
INFIX EXPRESSION    1*2+(4/2)
POSTFIX EXPRESSION  1 2 * 4 2 / +
RESULT              4.0000000
```

###  **Simple POSTFIX Examples:**

------------------------------------------------------------

**Note:** postfix expressions (reversed polish notation expressions such as _1 2 +_ or _1 2 / 3 4 * 3 / +_, where operators are on the right and operands on the left, must be provided as a normalized, space separated expression. This includes parentheses and function argument separator commas. So, while **infix** expressions such as _pow( 2, 4)_ ('pow' is a function and the expression means 2 in the power of 4, spacing is inconsistent and irrelevant), the **postfix** equivalent expression must be normalized and space separated, such as _2 4 pow_.

* **Example:** To evaluate a well formatted, space separated **postfix** expression:

```
String postfix = "1 2 *"; // note the spaces between the expression elements
BigDecimal result = RPNCalculator.accept(postfix).thenCalculate();
```

* This will produce a result of _2.0000000_


* **Example:** To evaluate a well formatted, space separated **postfix** expression **and** print an _INFO_ logging level console message with the detailed calculation parameters:

```
String postfix = "1 2 *"; // note the spaces between the expression elements
BigDecimal result = RPNCalculator.withDefaults().accept(postfix).doPrint().thenCalculate();
```
* This will produce a result of _2.0000000_ and the console _INFO_ logging level printed report will look something like this:

```
CALCULATION PARAMETERS
----------------------
CONTEXT
  class             org.silvermania.rpn.support.CalculationContext
  mathContext       DECIMAL32 precision=7 roundingMode=HALF_EVEN
  constants         PI,e,π
  operators         ^,!,√,*,×,/,÷,%,mod,+,−,-
  funtions          sin,cos,tan,sqrt,min,max,avg,pct,pow,sum
CALCULATOR
  class             org.silvermania.rpn.postfix.calculator.RPNCalculator
  context           default
POSTFIX EXPRESSION  1 2 *
RESULT              2.0000000
```

###  **Customizing the RPNCalculator Using INFIX Expressions Examples:**

------------------------------------------------------------

* **Example:** To evaluate **infix** arithmetic expressions, with a customized _CaclulationContext_ which uses a _java.math.MathContext#DECIMAL64_ (decimal precision of 16 and _java.math.RoundingMode#HALF__EVEN), instead of the default decimal precision of 7 (**and** to print an _INFO_ logging level console message with the detailed calculation parameters):

```
String infix = "1 * 2";
CalculationContext context = CalculationContext.DECIMAL64_CONTEXT; // DECIMAL64_CONTEXT has 16 decimal places and RoundingMode of HALF__EVEN
BigDecimal result = RPNCalculator.withContext(context).convert(infix).doPrint().thenCalculate();
```

* This will produce a result of _2.0000000000000000_ and the console _INFO_ logging level printed report will look something like this:

```
CALCULATION PARAMETERS
----------------------
CONTEXT
  class             org.silvermania.rpn.support.CalculationContext
  mathContext       DECIMAL64 precision=16 roundingMode=HALF_EVEN
  constants         π,e,PI
  operators         ^,!,√,*,×,/,÷,%,mod,+,−,-
  funtions          sin,cos,tan,sqrt,min,max,avg,pct,pow,sum
CALCULATOR
  class             org.silvermania.rpn.postfix.calculator.RPNCalculator
  context           customized
INFIX EXPRESSION    1 * 2
POSTFIX EXPRESSION  1 2 *
RESULT              2.0000000000000000
```

* **Example:** To evaluate **infix** arithmetic expressions, with a customized _CaclulationContext_ which uses a decimal precision of 2 and _java.math.RoundingMode#HALF__UP, instead of the default decimal precision of 7 (**and** to print an _INFO_ logging level console message with the detailed calculation parameters):

```
String infix = "1 * 2";
CalculationContext context = CalculationContext.newInstance();
context.setRoundingMode(java.math.RoundingMode.HALF_UP);
context.setPrecision(2);
BigDecimal result = RPNCalculator.withContext(context).convert(infix).doPrint().thenCalculate();
```
* This will produce a result of _2.00_ and the console _INFO_ logging level printed report will look something like this:

```
CALCULATION PARAMETERS
----------------------
CONTEXT
  class             org.silvermania.rpn.support.CalculationContext
  mathContext       CUSTOM precision=2 roundingMode=HALF_UP
  constants         PI,π,e
  operators         ^,!,√,*,×,/,÷,%,mod,+,−,-
  funtions          sin,cos,tan,sqrt,min,max,avg,pct,pow,sum
CALCULATOR
  class             org.silvermania.rpn.postfix.calculator.RPNCalculator
  context           customized
INFIX EXPRESSION    1 * 2
POSTFIX EXPRESSION  1 2 *
RESULT              2.00
```

###  **Customizing the RPNCalculator Using POSTFIX Expressions Examples:**

------------------------------------------------------------

* **Example:** To evaluate **postfix** expressions, with a customized _CaclulationContext_ which uses a _java.math.MathContext#DECIMAL64_ (decimal precision of 16 and _java.math.RoundingMode#HALF__EVEN), instead of the default decimal precision of 7 (**and** to print an _INFO_ logging level console message with the detailed calculation parameters):

```
String postfix = "1 2 *";
CalculationContext context = CalculationContext.DECIMAL64_CONTEXT; // DECIMAL64_CONTEXT has 16 decimal places and RoundingMode of HALF__EVEN
BigDecimal result = RPNCalculator.withContext(context).accept(postfix).doPrint().thenCalculate();
```
* This will produce a result of _2.0000000000000000_ and the console _INFO_ logging level printed report will look something like this:

```
CALCULATION PARAMETERS
----------------------
CONTEXT
  class             org.silvermania.rpn.support.CalculationContext
  mathContext       DECIMAL64 precision=16 roundingMode=HALF_EVEN
  constants         π,e,PI
  operators         ^,!,√,*,×,/,÷,%,mod,+,−,-
  funtions          sin,cos,tan,sqrt,min,max,avg,pct,pow,sum
CALCULATOR
  class             org.silvermania.rpn.postfix.calculator.RPNCalculator
  context           customized
POSTFIX EXPRESSION  1 2 *
RESULT              2.0000000000000000
```

* **Example:** To evaluate **postfix** arithmetic expressions, with a customized _CaclulationContext_ which uses a decimal precision of 2 and _java.math.RoundingMode#HALF__UP, instead of the default decimal precision of 7 (**and** to print an _INFO_ logging level console message with the detailed calculation parameters):

```
String postfix = "1 2 *";
CalculationContext context = CalculationContext.newInstance();
context.setRoundingMode(java.math.RoundingMode.HALF_UP);
context.setPrecision(2);
BigDecimal result = RPNCalculator.withContext(context).accept(postfix).doPrint().thenCalculate();
```
* This will produce a result of _2.00_ and the console _INFO_ logging level printed report will look something like this:

```
CALCULATION PARAMETERS
----------------------
CONTEXT
  class             org.silvermania.rpn.support.CalculationContext
  mathContext       CUSTOM precision=2 roundingMode=HALF_UP
  constants         e,PI,π
  operators         ^,!,√,*,×,/,÷,%,mod,+,−,-
  funtions          sin,cos,tan,sqrt,min,max,avg,pct,pow,sum
CALCULATOR
  class             org.silvermania.rpn.postfix.calculator.RPNCalculator
  context           customized
POSTFIX EXPRESSION  1 2 *
RESULT              2.00
```

###  **Advanced: Registering new constants, functions and operators**

------------------------------------------------------------

* **Example:** To add an imaginary constant _'~'_ say, to represent AC line voltage with the value of 120 (volts), we need to register it on the calculation context, and then pass the context to the calculator.

```
CalculationContext context = CalculationContext.DECIMAL64_CONTEXT; // DECIMAL64_CONTEXT has 16 decimal places and RoundingMode of HALF__EVEN
context.registerConstant("~", BigDecimal.valueOf(120));
String infix = "~ * (1/√2)"; // now we can calculate rms value
BigDecimal result = RPNCalculator.withContext(context).convert(infix).doPrint().thenCalculate();
```
* This will produce a result of _84.8528115_ and the console _INFO_ logging level printed report will look something like this (notice the new constant added):

```
CALCULATION PARAMETERS
----------------------
CONTEXT
  class             org.silvermania.rpn.support.CalculationContext
  mathContext       DECIMAL32 precision=7 roundingMode=HALF_EVEN
  constants         π,e,PI,~
  operators         ^,!,√,*,×,/,÷,%,mod,+,−,-
  funtions          sin,cos,tan,sqrt,min,max,avg,pct,pow,sum
CALCULATOR
  class             org.silvermania.rpn.postfix.calculator.RPNCalculator
  context           customized
INFIX EXPRESSION    ~ * (1/√2)
POSTFIX EXPRESSION  ~ 1 2 √ / *
RESULT              84.8528160
```

* BTW, we can also use the infix input expression ** ~ * [1/sqrt(2)]** and expect the same result. The unary function **sqrt(2)** and the unary operator **√**, have the same meaning and functionality. Now we're ready to register a new unary function, **rms(x)** to produce the RMS value of any AC voltage.


------------------------------------------------------------


* **Example:** To add an imaginary function _'rms(x)'_ say, to calculate root mean square AC line voltage, we need to register it on the calculation context, and then pass the context to the calculator.

```
CalculationContext context = CalculationContext.newInstance();
context.registerConstant("~", BigDecimal.valueOf(120));
Function<BigDecimal[], BigDecimal> function = arr -> arr[0].multiply(BigDecimal.ONE.divide(BigDecimal.valueOf(Math.sqrt(2)),context.getPrecision(),context.getRoundingMode()));
context.registerFunction("rms", Multiplicity.UNARY, function);
String infix = "rms(~)"; // we can now use our function and constant to calculate any rms value
BigDecimal result = RPNCalculator.withContext(context).convert(infix).doPrint().thenCalculate();
```

* This will produce a result of _84.8528160_ and the console _INFO_ logging level printed report will look something like this (notice the new constant added):

```
CALCULATION PARAMETERS
----------------------
CONTEXT
  class             org.silvermania.rpn.support.CalculationContext
  mathContext       DECIMAL32 precision=7 roundingMode=HALF_EVEN
  constants         π,e,PI,~
  operators         ^,!,√,*,×,/,÷,%,mod,+,−,-
  funtions          sin,cos,tan,sqrt,min,max,avg,pct,pow,sum,rms
CALCULATOR
  class             org.silvermania.rpn.postfix.calculator.RPNCalculator
  context           customized
INFIX EXPRESSION    rms(~)
POSTFIX EXPRESSION  ~ rms
RESULT              84.8528160
```

* Notice both the new constant **~** and new function **rms** appear in the now perfectly configured calculation context

* The assignment `Function<BigDecimal[], BigDecimal> function = arr -> arr[0].multiply(BigDecimal.ONE.divide(BigDecimal.valueOf(Math.sqrt(2)),context.getPrecision(),context.getRoundingMode()));` deserves some attention.
* it is a java.util.Function that accepts an array of BigDecimal arguments and returns a BigDecimal result. Since the **rms** function is an UNARY function, the array only contains one element in index 0.
* The part after the **->** is basically BigDecimal lingo to perform the calculation **v * (1/√2)**. The division requires precision and rounding mode, and this is supplied by our calculation context.
* The function registration code can become shorter using **static imports** for the enum _Multiplicity_ and BigDecimal.ONE, BigDecimal.valueOf and written as:
* `context.registerFunction("rms", UNARY, arr -> arr[0].multiply(ONE.divide(valueOf(Math.sqrt(2)),context.getPrecision(),context.getRoundingMode())));`
* or, even concise, if we know **1/√2** is **0.7071068** (which is not a bad idea to define as a constant):
* `context.registerFunction("rms", UNARY, arr -> arr[0].multiply(valueOf(0.7071068)));`


* Finally, we can revise the entire example and register our constant and function like this:

```
CalculationContext context = CalculationContext.newInstance();
context.registerConstant("~", BigDecimal.valueOf(120));
context.registerFunction("rms", UNARY, arr -> arr[0].multiply(valueOf(0.7071068)));
BigDecimal result = RPNCalculator.withContext(context).convert("rms(~)").doPrint().thenCalculate();
```

* For this to compile, we'd need the following imports:

```
import static java.math.BigDecimal.valueOf;
import static org.silvermania.rpn.support.Multiplicity.UNARY;
import org.silvermania.rpn.support.CalculationContext;
import org.silvermania.rpn.postfix.calculator.RPNCalculator;
import java.math.BigDecimal;
```

------------------------------------------------------------

* **Example:** To add a new operator _'r'_ say, to represent '**r**eciprocal' (1/x), we have to define it's symbol ('r'), precedence ('√' and '!' = **4**, '×', '÷' and '%' = **3**, '+' and '−' = **2**), multiplicity (UNARY or BINARY) and associativity (LEFT or RIGHT). We also have to define the operation function for the operator and then, register it on the calculation context, and pass the context to the calculator.

```
CalculationContext context = CalculationContext.newInstance();
context.registerOperator("r", 4, Associativity.LEFT, Multiplicity.UNARY,(arr) -> BigDecimal.ONE.divide(arr[0], context.getPrecision(),context.getRoundingMode()));
BigDecimal result = RPNCalculator.withContext(context).convert(infix).doPrint().thenCalculate();
```

* Using static imports this can get a little more concise:

```
context.registerOperator("r", 4, LEFT, UNARY,(arr) -> ONE.divide(arr[0], context.getPrecision(),context.getRoundingMode()));
```

* This will produce a result of _0.2500000_ and the console _INFO_ logging level printed report will look something like this (notice the new constant added):

```
CALCULATION PARAMETERS
----------------------
CONTEXT
  class             org.silvermania.rpn.support.CalculationContext
  mathContext       DECIMAL32 precision=7 roundingMode=HALF_EVEN
  constants         PI,π,e
  operators         ^,!,√,*,×,/,÷,%,mod,+,−,-,r
  funtions          sin,cos,tan,sqrt,min,max,avg,pct,pow,sum
CALCULATOR
  class             org.silvermania.rpn.postfix.calculator.RPNCalculator
  context           customized
INFIX EXPRESSION    r 4
POSTFIX EXPRESSION  4 r
RESULT              0.2500000
```

------------------------------------------------------------

* **Example:** Finding an Angle in a Right Angled Triangle: We can find an unknown angle in a right-angled triangle, as long as we know the lengths of two of its sides. We have a special phrase "SOHCAHTOA" to help us, and we use the **SOH** acronym like this: _sin(θ) = Opposite / Hypotenuse_. To extract _θ_, we'll need an **inverse sine** function defined as _sin^-1(Opposite/Hypotenuse)_. In this example we'll use the fluent API and calculation context chainability to define both _Opposite_ and _Hypotenuse_, as well as the **isin** function - then calculate the _30º_ angle:

```
context.addVariable("oppo", 3.33D)
       .addVariable("hypo", 6.66D)
       .registerFunction("isin", UNARY,
               arr -> context.round(Math.toDegrees(Math.asin(arr[0].doubleValue()))));
BigDecimal angle = RPNCalculator.withContext(context).accept("oppo hypo ÷ isin").doPrint().thenCalculate();
//BigDecimal angle = RPNCalculator.withContext(context).convert("isin(oppo÷hypo)").thenCalculate();

```

* This will produce a result of _30.0000000_ and the console _INFO_ logging level printed report will look something like this (notice the new variables and function):


```
CALCULATION PARAMETERS
----------------------
CONTEXT
  class             org.silvermania.rpn.support.CalculationContext
  mathContext       DECIMAL32 precision=7 roundingMode=HALF_EVEN
  constants         e,π,PI,oppo,hypo
  operators         ^,!,√,*,×,/,÷,%,+,−,-
  funtions          sin,cos,tan,min,max,avg,pct,sum,log,sin,cos,tan,min,max,avg,pct,sum,log,isin
CALCULATOR
  class             org.silvermania.rpn.postfix.calculator.RPNCalculator
  context           customized
POSTFIX EXPRESSION  oppo hypo ÷ isin
RESULT              30.0000000
```

------------------------------------------------------------


### Prerequisites

To run the software you will need to install:

* OpenJDK >= 12.0.1 (<https://jdk.java.net/12/> )
* Apache Maven >= 3.6.1 (https://maven.apache.org/download.cgi)

To modify, extend, develop this software you will need Eclipse IDE for Java development:

* Eclipse >= jee-2019-06-RC1 (<https://www.eclipse.org/downloads/packages/release/2019-06/rc1>)

### Installing

After obtaining the software source code and downloading it to a local directory follow these steps:

* CD into the installation directory

```
tom@localhost:/workspaces/jee-2019-06-RC1$ cd rpn
```
* build the main rpn project, run the tests and install it to the local maven repository:

```
tom@localhost:/workspaces/jee-2019-06-RC1/rpn$ mvn install
```
* to build the main project and generate the project site and reports without installing it to the local maven repository:

```
tom@localhost:/workspaces/jee-2019-06-RC1/rpn$ mvn clean package
tom@localhost:/workspaces/jee-2019-06-RC1/rpn$ mvn site
```
* **Note** however, you cannot run the example project test cases without first installing the main rpn project to the local repository.


* Installing the rpn calculator main project will let you use it in your code so you could do, for example, this:

```
import java.math.BigDecimal;

import org.silvermania.rpn.postfix.calculator.RPNCalculator;

public class Main {

    public static void main(String[] args) {
        // assume a radius of 12 and we want the circle area...
        BigDecimal area = RPNCalculator.convert("pow(π*12,2)").thenCalculate(); // converts to postfix "π 12 * 2 pow" and calculates 1421.2230305
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
        BigDecimal area = RPNCalculator.accept("π 12 * 2 pow").thenCalculate(); // calculates 1421.2230305
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
        BigDecimal area = RPNCalculator.withContext(context).convert("pow(π*r,2)").thenCalculate(); // converts to postfix "π r * 2 pow" and calculates 1421.2230305
        System.out.format("area: %s%n",area);
    }

}
```

## Running the tests

* Any of the _mvn package_, _mvn install_, _mvn site_, _mvn test_ commands will run the unit tests in any of the sub modules. The recommended way to run the tests and generate the reports in case the main project **is already installed** in the local maven repository, is to run the following command from the root of the main project:

```
tom@localhost:/workspaces/jee-2019-06-RC1/rpn$ mvn clean site
```

* The recommended way to run the tests and generate the reports in case the main project **is NOT installed** in the local maven repository, is to run the following commands from the root of the main project:

```
tom@localhost:/workspaces/jee-2019-06-RC1/rpn$ mvn clean package
tom@localhost:/workspaces/jee-2019-06-RC1/rpn$ mvn site
```

* Reports are generated in the _rpn/target/site_ sub directory and are accessible via the standard _rpn/target/site/index.html_ page


* **Note** however, that if the main project is not installed in the local maven repository, you will not be able to build and run the tests in modules _infix.converter_ and _postfix.calculator_ because they depend on the API module _postfix.support_ which is not in the local repository. You will also not be able to build or test the example project _calculator.example_ under the main project's root directory.


* For running the tests in development mode, you could install only the required dependencies for each module you are working on. Instructions are in the _pom.xml_ file of each sub-module and are module specific, but the essence is that if you want to build the _infix.converter_ sub-module for example, which, in this case depends on the common API sub-module _postfix.support_, you will need, at the very minimum, to install it's dependencies as follows:

```
tom@localhost:/workspaces/jee-2019-06-RC1/rpn$ mvn install -pl rpn.support -am
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
tom@localhost:/workspaces/jee-2019-06-RC1/rpn$ cd rpn.support
tom@localhost:/workspaces/jee-2019-06-RC1/rpn/rpn.support/$mvn package site
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


## Authors

* **T.N.Silverman** - *Initial work* - [Silvermania](https://github.com/silvermania)

See also the list of [contributors](CONTRIBUTORS.md) who participated in this project.


------------------------------------------------------------



## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details


------------------------------------------------------------



## Acknowledgments

* **Libraries:** This project uses the Simple Logging Facade for Java [SLF4J](https://www.slf4j.org/index.html) by **qos.ch** and their [LOGBACK](https://logback.qos.ch/) implementation.


* **Inspiration:** Algorithms and concepts: [Chris J.](https://www.chris-j.co.uk/parsing.php), and [Wikipedia - RPN](https://en.wikipedia.org/wiki/Reverse_Polish_notation)

