package com.dukascopy.api.drawings;


import java.util.Map;

public interface IDecoratedChartObject extends IChartDependentChartObject {
    public enum Decoration {
        None,
        Arrow,
        FilledArrow,
        SharpArrow,
        Circle
    }
    
    public enum Placement {
        Beginning,
        End
    }

	void setDecorations(Map<Placement, Decoration> decorations);

    Map<Placement, Decoration> getDecorations();
    
    /**
     * Sets new decoration for current figure
     * 
     * @param placement one of <code>Placement</code> constants
     * @param decoration one of <code>Decoration</code> constants
     * @see Decoration
     * @see Placement
     */
    void setDecoration(Placement placement, Decoration decoration);
    
    /**
     * Removes all decorations from current chart object
     */
    void removeDecorations();
}
