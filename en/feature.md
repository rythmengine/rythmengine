# Features
    
Rythm is yet another Java Template Engine. It provides an easy to use, super fast and general purpose template engine to Java programmer.

### Developer oriented

Rythm is created by developer for developers and the user experience is the number one concern of the product. The beauty of simplicity is been built into the product, from API to the template syntax.

Code is better than words. Let's take a look at some sample usage of Rythm API in Java program:


    // inline template
    String s = Rythm.render("Hello @who", "world");
    
    // external file template
    String s = Rythm.render("hello.txt", "world");
    
    // pass render arguments by position
    String s = Rythm.render("Hello @who", "world");
    
    // pass render arguments by name
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("who", "world");
    String s = Rythm.render("Hello @who", params);

    // or pass by name with a bit more fancy
    NamedParams np = NamedParams.instance;
    String s = Rythm.render("hello @who", np.from(np.pair("who", "world")));
    
And here is a Rythm template sample:

    @// template line comment
    
    @**
     * template block comment 
     * can across lines
     *@
    
    @// declare the arguments used in this template
    @args List<Order> orders, int maxLines, User user
    
    <h1>Order Manager</h1>
    
    @ifNot (user.hasRole("order-manager")) {
        <p>You don't have access to this page</p>
        @return @// break the template execution with @return
    }
    
    <div id="order-list">
    @for(Order order : orders) {
        @if(order_index >= maxLines) {
            <a href="#" id="load-more">Click here to load more order...</a>
            @break @// break the loop when maximum line reached
        }
        @**
         * loop variables used here: 
         * _parity: "odd" or "even" depends on current loop index
         * _isFirst: true if this is the first element
         * _isLast: true if this is the last element in the loop
         *@
        <div class='order @order_parity @(order_isFirst ? "first" : "") @(order_isLast ? "last" : "")'>
            <h3>@order.getName()</h3>
            @if (order.closed()) {
                <div>closed</code>
            } else {
                <div>...</div>
            }
        </div>
    }
    </div>

As shown above Rythm use a single special character `@` to introduce all syntax elements, including comments, flow control, variable evaluation. The idea is borrowed from [.Net Razor](http://weblogs.asp.net/scottgu/archive/2010/07/02/introducing-razor.aspx), and yes Razor inspired me to create Rythm because of this simple elegant syntax.

Another design philosophy of Rythm is to make it native to Java programmer as much as possible. Generally speaking, an experienced Java programmer shouldn't have any difficulty to read and understand a Rythm template even if he/she has never known Rythm before, and they should be able to start working on Rythm template in a few minutes.

### high performance

Being different from dynamic engines like [Velocity](http://velocity.apache.org/) and [FreeMarker](http://freemarker.sourceforge.net/), Rythm is a static typed engine and compile your template source into java byte code, and thus very fast. Based on the result of [this benchmark test](http://www.screenr.com/OE07), Rythm is one of the fastest template engines in Java world. HTTL and Jamon are the only ones faster than Rythm at the moment. Needness to say HTTL and Jamon are all static typed engines.

![benchmark-image](img/benchmark.png)

### General purpose

Rythm is designed as a general purpose template engine. It allows you to generate html page, xml file, source code, SQL script, email content and any other kind of **text based artifacts**.

### Template auto reload

Rythm can be started in 2 different modes: **prod**(Product) and **dev**(Development). 

When running in development mode, Rythm monitors template source files, and automatically reload the content once one changes. This process is very fast, usually you won't be able to notice the delay. 

Auto reload feature is disabled when Rythm is running in product mode to maximize the performance gain.

### Extensibility and reusability

Rythm provides varieties of ways for template author to reuse and extend their template library, including:

* **External tags**: Most common and powerful way to extend template library. Each single template file is a rythm tag and can be called from any other templates, and even call back calling template from the tag template.
* **Internal tags**: Defining an internal tag is as simple as defining a method of a class
* **Macro**: The fastest way to reuse code block inside one template file
* **Include directive**: Including other template content in place at parsing time, and reuse internal tags defined in the including template.
* **Java Extension**: A mechanism to attach new methods to Java types in expression evaluation

### Security

By default Rythm runs template without any restriction, this is good as long as the template code is under the control of the project. However, when you want to accept and execute template source code from untrusted source, e.g. end user input, you need to make sure the code won't break through your environment. 

Rythm provides a restricted environment named "Substitute Mode" to alleviate the security concern raised when you needs to process templates coming from untrusted source. Note this mode is a very restricted and most features are tailored due to security concern, name a few of them: _Expression Evaluation_, _Scripting_ and _Free style looping_. All these means you have absolute security to run whatever templates without worrying about your system get broken. To render templates in "Substitute Mode" simply use `substitute` instead of `render` API:

    String s = Rythm.substitute(unTrustedTemplate, ...)

In case you really want to provide features beyond the limits of "Substitute Mode" to external template provider, Rythm provides sandbox mode to make sure the untrusted template code is running in a secure way. The sandbox mode also prevent infinite loop from eating up your resources. As usual Rythm provide a simple API to enter Sandbox:

    String s = Rythm.sandbox().render(unTrustedTemplate, ...);
    
Please be noted that sandbox is not a free lunch. When Rythm is running in sandbox mode, it takes about 40% more time to render the same template with the same input compare to normal mode.   
    
### Rich functionality

Rythm provides wide range of templating features includes

* Template layout management
* External and internal tags
* Tag callback (external tag only)
* Java extension
* Template cache
* Recursive tag invocation

