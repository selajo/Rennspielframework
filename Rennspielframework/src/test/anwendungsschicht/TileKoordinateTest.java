package anwendungsschicht;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TileKoordinateTest {

    @Test
    public void test_checkInArea_isFalse() {
        List<TileKoordinate> list = new ArrayList<>();
        list.add(new TileKoordinate(1,3));
        list.add(new TileKoordinate(2, 5));
        TileKoordinate tile = new TileKoordinate(1, 4);

        assertFalse(tile.isInArea(list));
    }

    @Test
    public void test_checkInArea_isTrue() {
        List<TileKoordinate> list = new ArrayList<>();
        list.add(new TileKoordinate(1,3));
        list.add(new TileKoordinate(2, 5));
        TileKoordinate tile = new TileKoordinate(2, 5);

        assertTrue(tile.isInArea(list));
    }
}
