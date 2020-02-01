package com.dukascopy.api.plugins.widget;

import javax.swing.SwingConstants;

/**
 * Widget properties.
 * Available with JForex-API 3.0.
 *
 */
public class WidgetProperties {

	public enum Position {
		TOP("Top", SwingConstants.NORTH),
		BOTTOM("Bottom", SwingConstants.SOUTH),
		LEFT("Left", SwingConstants.WEST);

		private String name;
		private int id;

		Position(String name, int id) {
			this.name = name;
			this.id = id;
		}

		public int getId() {
			return id;
		}

		@Override
		public String toString() {
			return name;
		}
	}
	
	private int position = SwingConstants.NORTH;	
	
	private WidgetProperties() {}
	
	public static WidgetProperties newInstance(){
		return new WidgetProperties();
	}
	
	/**
	 * Preferable widget position ranging from SwingConstants.NORTH to SwingConstants.NORTH_WEST
     * @param position position
	 * @return this
	 */
	public WidgetProperties position(int position){
		this.position = position;
		return this;
	}

	public WidgetProperties position(Position position) {
		this.position = position.getId();
		return this;
	}
	
	/**
	 * Preferable widget position ranging from SwingConstants.NORTH to SwingConstants.NORTH_WEST
	 * @return position
	 */
	public int getPosition(){
		return position;
	}
}
