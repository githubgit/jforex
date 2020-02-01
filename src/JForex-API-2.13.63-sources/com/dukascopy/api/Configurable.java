/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields marked with this annotation will be shown in dialog before strategy start.<br>
 * Values from that dialog will be set to fields before calling onStart method.<br>
 * Depending on the parameter type it gets represented differently in the parameters dialog.<br>
 * <br>
<table style="th, td { padding: 5px; }" border="1">
   <caption style="text-align:left">The following parameter types are supported:<br><br></caption>
   <tbody>
      <tr>
         <td><b>Parameter type</b></td>
         <td><b>Applicable Java types</b></td>
         <td><b>Representation</b></td>
         <td><b>Remote mode differences</b></td>
      </tr>
      <tr>
         <td>Number</td>
         <td><i>int</i>, <i>double</i>, <i>short</i>, <i>long</i>, <i>Integer</i>, <i>Double</i>, <i>Short</i>, <i>Long</i></td>
         <td>Number field with a modifiable step size</td>
         <td></td>
      </tr>
      <tr>
         <td>Boolean</td>
         <td><i>boolean</i>, <i>Boolean</i></td>
         <td>Checkbox</td>
         <td></td>
      </tr>
      <tr>
         <td>String</td>
         <td><i>String</i></td>
         <td>Text field</td>
         <td></td>
      </tr>
      <tr>
         <td>File</td>
         <td><i>java.util.File</i></td>
         <td>Text field with a path and a file chooser</td>
         <td></td>
      </tr>
      <tr>
         <td>Date</td>
         <td><i>java.util.Calendar</i>, <i>java.util.Date</i>, <i>long </i>and <i>Long </i>with <i>Configurable.datetimeAsLong = true</i></td>
         <td>Date picker</td>
         <td></td>
      </tr>
      <tr>
         <td>Color</td>
         <td><i>java.util.Color</i></td>
         <td>Color picker</td>
         <td></td>
      </tr>
      <tr>
         <td>Constants</td>
         <td>any <i>enum </i>or <i>Enum</i> or a class containing self-typed <b>public static final</b> fields </td>
         <td>Single-selection combobox</td>
         <td>User-defined constants via <i>Configurable.options</i></td>
      </tr>
      <tr>
         <td>Collection of constants</td>
         <td><i>java.util.Collection</i> of any <i>enum </i>or <i>Enum</i> or a class containing self-typed <b>public static final</b> fields </td>
         <td>Multi-selection dialog</td>
         <td>User-defined constants are not allowed</td>
      </tr>
      <tr>
         <td>IFeedDescriptor</td>
         <td><i>IFeedDescriptor</i> implementations from <i>com.dukascopy.api.feed.util</i></td>
         <td>Data feed chooser dialog</td>
         <td></td>
      </tr>
   </tbody>
</table>
<p>Consider the following parameter usage examples:
<pre><code>
   {@literal @}Configurable(value = "int param", stepSize = 3)
    public int intParam = 1;
   {@literal @}Configurable(value = "double param", stepSize = 0.5)
    public double doubleParam = 0.5;
   {@literal @}Configurable("bool param")
    public boolean boolParam = true;
   {@literal @}Configurable("text param")
    public String textParam = "some text";
   {@literal @}Configurable("")
    public File file = new File(".");
   {@literal @}Configurable(value="current time", description="default is current time")
    public Calendar currentTime = Calendar.getInstance();
   {@literal @}Configurable("")
    public Color color = new Color(100, 100, 100);
   {@literal @}Configurable("instrument (enum)")
    public Instrument instrument = Instrument.EURUSD;
   {@literal @}Configurable("")
    public{@literal Set<Instrument>} instruments = new{@literal HashSet<Instrument>}(
            Arrays.asList(new Instrument[] {Instrument.EURUSD, Instrument.AUDCAD}) 
    );
   {@literal @}Configurable("")
    public IFeedDescriptor renkoFeedDescriptor = new RenkoFeedDescriptor(Instrument.EURUSD, PriceRange.TWO_PIPS, OfferSide.ASK);
    
    //date/time usage possibilities
    
    private static Calendar myCalendar;
    static {
        myCalendar = Calendar.getInstance();
        myCalendar.set(2012, Calendar.JULY, 17, 14, 30, 00);
    }
    
   {@literal @}Configurable(value="particular time", description="17th july 14:30")    
    public Calendar particularTime = myCalendar;
    
    private static Calendar calTodayAt5am;
    static {
        calTodayAt5am = Calendar.getInstance();
        calTodayAt5am.set(Calendar.HOUR_OF_DAY, 5);
        calTodayAt5am.set(Calendar.MINUTE, 0);
        calTodayAt5am.set(Calendar.SECOND, 0);
    }
    
   {@literal @}Configurable(value="time in millis", description="default is today at 5am", datetimeAsLong=true)    
    public long timeInMillis = calTodayAt5am.getTimeInMillis();
    
    //custom enum
    
    enum Mode { 
        BUY,
        SELL,
        NONE
    }

   {@literal @}Configurable("mode (enum param)")
    public Mode mode = Mode.BUY;

    //custom class with self-typed constants
    
    static class Person {
        
        public static final Person FOO = new Person("foo");
        
        public static final Person BAR = new Person("bar");
        
        public final String name;
        
        public Person(String name){
            this.name = name;
        }
        
       {@literal @}Override 
        public String toString(){
            return name;
        }
    }
    
   {@literal @}Configurable("")
    public Person person;
    
   {@literal @}Configurable("")
    public{@literal List<Person>} persons;
</code></pre>
 *
 * @author Denis Larka
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Configurable {
    /**
     * Name of the field that will appear in dialog.
     *
     * @return value for field
     */
    String value();

    /**
     * This field is exclusively for File parameters,
	 * for other types of parameters fileType value will be ignored.
	 *
     * <p>
     * File parameters can be visualized in 2 ways:
     * </p>
     * <p>
     * <b>1.</b> textField and button("..."),
     * which allow you to specify file from your file system.
     * Do not use fileType or define it ="" to use this visualization
     * </p>
     * <p>
     * <b>2.</b> combobox with predefined list of file(s) from strategy directory.
     * fileType should not be empty to use this visualization.
     * In this case fileType is filter, which specifies what files to show in combo.<br>
     * e.g. fieldType="*" - return all your files from Strategy directory,<br>
     * fieldType="*.xml" - return only files with XML extension from Strategy directory<br>
     * </p>
     *
     * @return file name filter
     */
    String fileType() default "";

    /**
     * This field is suitable for parameter's types as : int, long or double.
     * For other types of parameters stepSize value will be ignored.
     * <br>
     * Value must be positive and greater then zero.
     * Default stepSize for int and long is 1, for double it's 0.5
     *
     * @return step value
     */
    double stepSize() default 0;

    /**
     * Setting this to true means field is obligatory.
     *
     * @return true - obligatory, default false
     */
    boolean obligatory() default false;
    
    /**
     * Setting this to true means field is read only and cannot be modified.
     *
     * @return true - read only, default false
     */
    boolean readOnly() default false;
    
    /**
     * A short description of annotated field.
     *
     * @return description of configurable field
     */
    String description() default "";
    
    /**
     * If true, then field contains date/time value presented as long.
     *
     * @return true - date/time is presented as long, default false
     */
    boolean datetimeAsLong() default false;
    
    /**
     * Determines possible values of a String field.
     * When used the field appears as a combo box.
     * Applicable only for String fields, otherwise ignored.
     * 
     * @return possible values
     */
    String[] options() default {};
    
    /**
     * Determines if the parameter is changeable during the strategy run.
     * 
     * @return true - modifiable, default false
     */
    boolean modifiable() default false;

    /**
     * If true, then property is saved in preset, but isn't shown in Settings dialog.
     *
     * @return true - hidden, default false
     */
    boolean hidden() default false;
}
