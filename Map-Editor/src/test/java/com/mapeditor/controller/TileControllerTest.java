package com.mapeditor.controller;

import com.mapeditor.model.TileReader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TileControllerTest {
    HashMap<Integer, Object[]> tileInformationen;
    HashMap<Integer, BufferedImage> spielfeldTiles;
    @Mock
    private TileReader mock;
    /*
    @Spy
    private TileController spy;

     */
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        tileInformationen = new HashMap<>();
        tileInformationen.put(0, new Object[] {"test"} );

        spielfeldTiles = new HashMap<>();
        BufferedImage image = new BufferedImage(1, 1 , BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 1);
        spielfeldTiles.put(0, image);
    }

    @Test
    public void test_getInstance_Typ() {
        TileController tileController = TileController.getInstance();
        assertTrue(tileController != null);
    }

    @Test
    public void test_datenEinlesen_tileInformationen() {
        TileReader mock = mock(TileReader.class);
        when(mock.tilesEinlesen()).thenReturn(tileInformationen);
        when(mock.ladeTileBilder()).thenReturn(spielfeldTiles);

        TileController tileController = TileController.getInstance();
        tileController.tileReader = mock;

        tileController.datenEinlesen();

        assertEquals("test", tileController.tileInformationen.get(0)[0]);
    }

    @Test
    public void test_datenEinlesen_spielFeldTiles() {
        TileReader mock = mock(TileReader.class);
        when(mock.tilesEinlesen()).thenReturn(tileInformationen);
        when(mock.ladeTileBilder()).thenReturn(spielfeldTiles);

        TileController tileController = TileController.getInstance();
        tileController.tileReader = mock;

        tileController.datenEinlesen();

        assertEquals(1, tileController.spielfeldTiles.get(0).getRGB(0, 0));
    }

/*
    @Test
    public void test_writeToFile_return() {
        TileReader mock = mock(TileReader.class);
        when(mock.writeToFile(anyString(), anyString())).thenReturn(true);

        TileController tileController = TileController.getInstance();
        tileController.checkpunkte = "check";
        tileController.startposition = "start";
        tileController.tileReader = mock;

        boolean actual = tileController.writeCompleteData("testMap", "text");

        assertTrue(actual);
        //verify(mock, times(1)).writeToFile("testMap.txt", "text");
        //verify(mock, times(1)).writeToFile("testMap_checkpoints.txt", "check");
        //verify(mock, times(1)).writeToFile("testMap_startpositionen.txt", "start");
    }

 */

    @Test
    public void test_ladeMap_Mocking() {
        TileReader mock = mock(TileReader.class);
        when(mock.ladeMap(anyString())).thenReturn(new int[][]{ {1}, {1}});

        TileController tileController = TileController.getInstance();
        tileController.tileReader = mock;

        int[][] actual = tileController.ladeMap("file");

        assertEquals(1, actual[0][0]);
        verify(mock, times(1)).ladeMap("file");
    }

    @Test
    public void test_addStartpunkt_alles_in_Ordnung() {
        int[] rows = new int[] { 1, 2 };
        int[] cols = new int[] { 3, 4 };

        TileController tileController = TileController.getInstance();
        tileController.startposition = "";
        tileController.addStartpunkte(rows, cols, 3);

        assertEquals("3 1 3\n3 2 3\n4 1 3\n4 2 3\n", tileController.getStartposition());
    }

    @Test
    public void test_addCheckpunkte_erstes_Mal() {
        int[] rows = new int[] { 1, 2 };
        int[] cols = new int[] { 3, 4 };

        TileController tileController = TileController.getInstance();
        tileController.checkpunkte = "";
        tileController.addCheckpunkte(rows, cols);

        assertEquals("4 2 1\n5 2 1\n4 3 1\n5 3 1\n", tileController.getCheckpunkte());
    }

    @Test
    public void test_addCheckpunkte_zweites_Mal() {
        int[] rows = new int[] { 0 };
        int[] cols = new int[] { 0 };

        TileController tileController = TileController.getInstance();
        tileController.checkpunkte = "";
        tileController.checkpunktCounter = 0;
        tileController.addCheckpunkte(rows, cols);
        tileController.addCheckpunkte(rows, cols);

        assertEquals("1 1 1\n1 1 2\n", tileController.getCheckpunkte());
    }

    @Test
    public void test_setter() {
        TileController tileController = TileController.getInstance();

        tileController.setStartposition("start");
        tileController.setCheckpunkte("check");

        assertEquals("start", tileController.getStartposition());
        assertEquals("check", tileController.getCheckpunkte());

        //Zuruecksetzen
        tileController.setStartposition("");
        tileController.setCheckpunkte("");
    }

}
