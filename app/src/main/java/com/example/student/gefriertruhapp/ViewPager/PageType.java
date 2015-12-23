package com.example.student.gefriertruhapp.ViewPager;

import com.example.student.gefriertruhapp.DetailFragments.ShelfDetailFragment;
import com.example.student.gefriertruhapp.ShelfRecyclerView.FridgeListFragment;

/**
 * Created by student on 23.12.15.
 */
public enum PageType {
    FridgeList("Gefriertruhe", 0),
    ShelfList("Lager", 1),
    ShoppingList("Einkaufsliste", 2)
    ;

    private final String text;
    private final int i;
    /**
     * @param text
     */
    private PageType(final String text, final int i) {
        this.text = text;
        this.i = i;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }

    public int getI(){
        return i;
    }
}
