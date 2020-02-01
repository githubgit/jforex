package com.dukascopy.api.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

/**
 * The interface represents a chart theme
 */
public interface IChartTheme extends Cloneable {

	/**  
	 * Predefined chart theme enumeration, use it in <code>IClientChartPresentationManager.getPredefinedTheme</code>
	 */
	enum Predefined {
		RED_GREEN_ON_WHITE("Green Red on White", true),
		BLUE_BLACK_ON_GRAY("Blue Black on Grey"),
		BLUE_BLACK_ON_WHITE("Blue Black on White"),
		BLUE_WHITE_ON_BLACK("Blue White on Black"),
        GREEN_RED_ON_GRAY("Green Red on Grey"),
        BLUE_RED_ON_WHITE("Blue Red on White"),
        BLUE_RED_ON_BLACK("Blue Red on Black"),
        DARK_BLUE_DEFAULT("Green Red on Dark Blue"),
        DARK_BLUE_ASIAN("Blue Red on Dark Blue");
		
		private final String name;
		private final boolean isDefault;
		
		private Predefined(String name, boolean isDefault){
			this.name = name;
			this.isDefault = isDefault;
		}
		
		private Predefined(String name){
			this(name, false);
		}
		
		public static Predefined getDefault(){
			for (Predefined predefined : Predefined.values()) {
				if (predefined.isDefault()) {
					return predefined;
				}
			}
			return null;
		}
		
		public String getName(){
			return name;
		}
		
		public boolean isDefault(){
			return isDefault;
		}	
		
		@Override
		public String toString(){
			return name;
		}		
	}
	
	/**
	 * Theme color settings	 
	 */	
	enum ColoredElement {
		DEFAULT								("Default colour"					),
		BACKGROUND							("Background"						),

		GRID								("Grid"								),
		DRAWING								("Drawing"							),
		ALERT                               ("Alert"                            ),
		OUTLINE								("Outline"							),
		OHLC								("OHLC"								),
		OHLC_BACKGROUND                     ("OHLC Background"                  ),
		META								("Meta Info"						),
		LAST_CANDLE_TRACKING_LINE			("Current best price line colour"	),
		LAST_OPPOSITE_CANDLE_TRACKING_LINE  ("Opposite best price line colour"),
		INDICATOR_LAST_VALUE_TRACKING_COLOR ("Indicator last value tracking line colour"),

		BID									("Bid"								),
		ASK									("Ask"								),
		NEUTRAL_BAR                         ("Neutral bar"                      ),
		LINE_UP                             ("Up"                               ),
		LINE_DOWN                           ("Down"                             ),
		BAR_UP								("Bar up"							),
		BAR_DOWN							("Bar down"							),
		DOJI_CANDLE                         ("Doji candle"                      ),
		CANDLE_BEAR							("Bear candle"						),
		CANDLE_BEAR_BORDER                  ("Bear candle border"               ),
		CANDLE_BULL							("Bull candle"						),
		CANDLE_BULL_BORDER                  ("Bull candle border"               ),
		YIN                                 ("Yin"                              ),
		YANG                                ("Yang"                             ),
		AREA                                ("Area"                             ),
		
		SECOND_INSTRUMENT                   ("2nd instrument"                   ),
		THIRD_INSTRUMENT                    ("3rd instrument"                   ),
		FOURTH_INSTRUMENT                   ("4th instrument"                   ),
		FIFTH_INSTRUMENT                    ("5th instrument"                   ),

		HT_BALANCE                          ("Balance"                          ),
		HT_EQUITY                           ("Equity"                           ),
		HT_PROFIT_LOSS                      ("P/L"                              ),
		
		AXIS_LABEL_FOREGROUND				("Label Foreground"					),
		AXIS_LABEL_BACKGROUND				("Label Background"					),
		AXIS_LABEL_BACKGROUND_ASK			("Ask Label Background"				),
		AXIS_LABEL_BACKGROUND_BID			("Bid Label Background"				),
		AXIS_PANEL_BACKGROUND               ("Axis Panel Background"            ),
        AXIS_PANEL_FOREGROUND               ("Axis Panel Foreground"            ),

		ORDER_TRACKING_LINE					("Position/External ID, P/L"    	),
		ORDER_LONG_POSITION_TRACKING_LINE   ("Long Position Tracking Line"      ),
		ORDER_SHORT_POSITION_TRACKING_LINE  ("Short Position Tracking Line"     ),
		ORDER_MERGING_LINE                  ("Position Merging Line"            ),
		ORDER_OPEN_SELL						("Arrow Short Open"					),
		ORDER_CLOSE_SELL					("Arrow Short Close"				),
		ORDER_OPEN_BUY						("Arrow Long Open"   				),
		ORDER_CLOSE_BUY						("Arrow Long Close"					),
		PERIOD_SEPARATORS					("Period Separators"				),
		
		@Deprecated TEXT                    ("Text"                             ),
        @Deprecated ODD_ROW                 ("Odd Row"                          ),
        @Deprecated EVEN_ROW                ("Even Row"                         ),
        @Deprecated CANDLE_BEAR_ROW         ("Candle Bear Row"                  ),
        @Deprecated CANDLE_BULL_ROW         ("Candle Bull Row"                  ),
		@Deprecated CANDLE_DOJI_FG          ("Doji candle foreground"           ),
        @Deprecated CANDLE_BEAR_FG          ("Bear candle foreground"           ),
        @Deprecated CANDLE_BULL_FG          ("Bull candle foreground"           );

		

		private String description;

		private ColoredElement(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	/**
	 * Theme text settings	 
	 */
	enum TextElement {
		DEFAULT		("Default"		),
		AXIS		("Axis labels"	),
		OHLC		("OHLC"			),
		META		("Meta info"	),
		ORDER		("Order info"	);

		private String description;

		private TextElement(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}
	
	/**
	 * Theme stroke settings
	 */
	enum StrokeElement {
		DEFAULT                             ("Default"                                  ),
		GRID_STROKE                         ("Grid Stroke"                              ),
		ORDER_LINES_STROKE                  ("Order Lines Stroke"                       ),
		LAST_CANDLE_TRACKING_LINE_STROKE    ("Last price tracking line Stroke"          ),
		INDICATOR_LAST_VALUE_TRACKING_STROKE("Indicator last value tracking line Stroke"),
		PERIOD_SEPARATORS_STROKE            ("Period Separators Stroke"                 );
		
		public static final BasicStroke BASIC_STROKE = new BasicStroke(
				1,
				BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL,
				0,
				null, // solid
				0
		);

		private String description;

		private StrokeElement(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	/**
	 * Sets theme name
	 * @param name theme name
	 * @return the theme itself
	 */
	IChartTheme setName(String name);
	
	/**
	 * Returns theme name
	 * @return theme name
	 */
	String getName();

	/**
	 * Sets the color of a colored chart element 
	 * 
	 * @param coloredElement chart element whose color needs to be changed
	 * @param color new color
	 * @return the theme itself
	 */
	IChartTheme setColor(ColoredElement coloredElement, Color color);
	
	/**
	 * Returns the color of a colored chart element
	 * 
	 * @param coloredElement chart element whose color needs to be returned
	 * @return the color of a colored chart element
	 */
	Color getColor(ColoredElement coloredElement);

	/**
	 * Sets the font of a text chart element
	 * 
	 * @param textElement chart element whose font needs to be changed
	 * @param font new font
	 * @return the theme itself
	 */
	IChartTheme setFont(TextElement textElement, Font font);
	
	/**
	 * Returns the font of a text chart element
	 * 
	 * @param textElement chart element whose font needs to be returned
	 * @return the font of a text chart element
	 */
	Font getFont(TextElement textElement);
	
	/**
	 * Sets the stroke of a stroke chart element
	 * 
	 * @param strokeElement chart element whose stroke needs to be changed
	 * @param stroke new stroke
	 * @return the theme itself
	 */
	IChartTheme setStroke(StrokeElement strokeElement, BasicStroke stroke);
	
	/**
	 * Returns the stroke of a stroke chart element
	 * 
	 * @param strokeElement chart element whose stroke needs to be returned
	 * @return the stroke of a stroke chart element
	 */
	BasicStroke getStroke(StrokeElement strokeElement);

	/**
	 * Clones a chart theme
	 * @return a cloned chart theme
	 */
	IChartTheme clone();
}