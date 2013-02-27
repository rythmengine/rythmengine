# Document Center

### [get]Get Rythm

You can get Rythm in 2 ways:

1. Download Rythm distribution pack by clicking [here](/dist/rythm-engine-1.0-b2-SNAPSHOT-dist.zip). or
1. Use the Maven repository as described below

### [maven]Maven repository

Add Rythm dependency to your `pom.xml` file:

    <dependency>
        <groupId>com.greenlaw110.rythm</groupId>
        <artifactId>rythm-engine</artifactId>
        <version>1.0-b2-SNAPSHOT</version>
    </dependency>
    
If you are using SNAPSHOT version of Rythm, make sure you have the following section in the `pom.xml` file also:

    <parent>
      <groupId>org.sonatype.oss</groupId>
      <artifactId>oss-parent</artifactId>
      <version>7</version>
    </parent>    
    
<br/>
    
<div class="alert">
For a complete maven2 sample application, please find it inside the sample folder of the <a href="/dist/rythm-engine-1.0-b2-SNAPSHOT-dist.zip">distribution package</a>, or browse it on the <a target="_blank" href="https://github.com/greenlaw110/Rythm/tree/1.0/samples/MavnSampleProject">Github</a>
</div>

### [tutorial]Tutorial

* [Set up your environment](/doc/tutorial#env)
* [The hello world](/doc/tutorial#hello)
* [Duke's Bookstore](/doc/tutorial#bookstore)

