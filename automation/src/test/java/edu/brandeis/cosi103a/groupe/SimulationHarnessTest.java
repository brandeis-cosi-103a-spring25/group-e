package edu.brandeis.cosi103a.groupe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.checkerframework.checker.units.qual.A;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import edu.brandeis.cosi.atg.api.Player;
import edu.brandeis.cosi.atg.api.Player.ScorePair;
import edu.brandeis.cosi.atg.api.PlayerViolationException;
import edu.brandeis.cosi103a.groupe.Engine.EngineHarness;
import edu.brandeis.cosi103a.groupe.Engine.GameEngine;
import edu.brandeis.cosi103a.groupe.Engine.SimulationHarness;
import edu.brandeis.cosi103a.groupe.Players.BigMoneyPlayer;
import edu.brandeis.cosi103a.groupe.Players.ConsolePlayer;
import edu.brandeis.cosi103a.groupe.Players.ourPlayer;

public class SimulationHarnessTest {
    private SimulationHarness harness;
    private ourPlayer ourplayer1;
    private ourPlayer ourplayer2;
    private ourPlayer ourplayer3;
    private Player player1;
    private Player player2;
    private Player player3;


    @Before
    public void setUp() {
        harness = new SimulationHarness(5); // Simulate 5 games per matchup
        ourplayer1 = mock(ourPlayer.class);
       
        ourplayer2 = mock(ourPlayer.class);
        ourplayer3 = mock(ourPlayer.class);
        ourplayer1.setPlayer(player1);
        ourplayer2.setPlayer(player2);
        ourplayer3.setPlayer(player3);


        when(ourplayer1.getPlayer().getName()).thenReturn("Player 1");
        when(ourplayer2.getPlayer().getName()).thenReturn("Player 2");
        when(ourplayer3.getPlayer().getName()).thenReturn("Player 3");
    }
    
    // Test that the SimulationHarness is initialized correctly
    @Test
    public void testInitialization() {
        assertNotNull(harness);
    }
    
    // Test that players can be added to the SimulationHarness
    @Test
    public void testAddOnePlayer() {
        ourplayer1.setPlayer(player1);

        harness.addPlayer(ourplayer1);
        assertEquals(1, harness.getPlayers().size());
    }
    
    // Test that multiple players can be added to the SimulationHarness
    @Test
    public void testAddTwoPlayer() {
        ourplayer1.setPlayer(player1);
        ourplayer2.setPlayer(player2);
        harness.addPlayer(ourplayer1);
        harness.addPlayer(ourplayer2);

        assertEquals(2, harness.getPlayers().size());
        assertTrue(harness.getPlayers().contains(ourplayer1));
        assertTrue(harness.getPlayers().contains(ourplayer2));
    }
    
    // Test that multiple players can be added to the SimulationHarness
    @Test
    public void testAddMultiplePlayers() {
        ourplayer1.setPlayer(player1);
        ourplayer2.setPlayer(player2);
        ourplayer3.setPlayer(player3);

        harness.addPlayer(ourplayer1);
        harness.addPlayer(ourplayer2);
        harness.addPlayer(ourplayer3);
        assertEquals(3, harness.getPlayers().size());
    }
    
    // Test that the simulation can run with at least two players
    @Test
    public void testRunWithLessThanTwoPlayers() throws Exception {

        ourplayer1.setPlayer(player1);

        harness.addPlayer(ourplayer1);

        // Capture console output
        var outputStream = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outputStream));

        harness.run();

        String output = outputStream.toString();
        assertTrue(output.contains("At least two players are required to run the simulation."));
    }

    // Test that the simulateMatchup method works correctly
    @SuppressWarnings("unchecked")
    @Test
    public void testSimulateMatchup() throws Exception {
    // Create a partial mock of SimulationHarness
    SimulationHarness spyHarness = org.mockito.Mockito.spy(harness);

    // Mock the GameEngine
    GameEngine mockEngine = mock(GameEngine.class);

    ourplayer1.setPlayer(player1);
    ourplayer2.setPlayer(player2);

    // Define the behavior of determineWinner
    ImmutableList<ScorePair> result1 = ImmutableList.of(new ScorePair(ourplayer1.getPlayer(), 10), new ScorePair(ourplayer2.getPlayer(), 5));
    ImmutableList<ScorePair> result2 = ImmutableList.of(new ScorePair(ourplayer1.getPlayer(), 5), new ScorePair(ourplayer2.getPlayer(), 10));
    ImmutableList<ScorePair> result3 = ImmutableList.of(new ScorePair(ourplayer1.getPlayer(), 10), new ScorePair(ourplayer2.getPlayer(), 5));
    ImmutableList<ScorePair> result4 = ImmutableList.of(new ScorePair(ourplayer1.getPlayer(), 10), new ScorePair(ourplayer2.getPlayer(), 5));
    ImmutableList<ScorePair> result5 = ImmutableList.of(new ScorePair(ourplayer1.getPlayer(), 10), new ScorePair(ourplayer2.getPlayer(), 5));

    when(mockEngine.determineWinner()).thenReturn(result1, result2, result3, result4, result5);

    // Override the creation of GameEngine in the simulateMatchup method

    doReturn(mockEngine).when(spyHarness).createGameEngine(ourplayer1, ourplayer2);

    // Add players to the harness


    spyHarness.addPlayer(ourplayer1);
    spyHarness.addPlayer(ourplayer2);

    // Simulate the matchup
    spyHarness.simulateMatchup(ourplayer1, ourplayer2);

    // Verify stats
    assertEquals(4, spyHarness.getTotalWins().get(ourplayer1).intValue());
    assertEquals(1, spyHarness.getTotalWins().get(ourplayer2).intValue());
    assertEquals(1, spyHarness.getTotalLosses().get(ourplayer1).intValue());
    assertEquals(4, spyHarness.getTotalLosses().get(ourplayer2).intValue());
    assertEquals(45, spyHarness.getTotalAp().get(ourplayer1).intValue()); // Total AP for player1
    assertEquals(30, spyHarness.getTotalAp().get(ourplayer2).intValue()); // Total AP for player2
    }  
    
    // Test that the overall results are calculated correctly
    @Test
    public void testOverallResults() throws Exception {
        Player player3 = (Player) new ConsolePlayer("BigMoney");
        ourplayer1.setPlayer(player1);
        ourplayer2.setPlayer(player2);
        ourplayer3.setPlayer(player3);
        

        harness.addPlayer(ourplayer1);
        harness.addPlayer(ourplayer2);
        harness.addPlayer(ourplayer3);

        // Simulate basic results
        harness.getTotalWins().put(ourplayer1, 5);
        harness.getTotalLosses().put(ourplayer1, 2);
        harness.getTotalAp().put(ourplayer1, 50);

        harness.getTotalWins().put(ourplayer2, 3);
        harness.getTotalLosses().put(ourplayer2, 4);
        harness.getTotalAp().put(ourplayer2, 30);

        harness.getTotalWins().put(ourplayer3, 1);
        harness.getTotalLosses().put(ourplayer3, 6);
        harness.getTotalAp().put(ourplayer3, 20);

        // Check win rates and averages
        double player1WinRate = (double) 5 / (5 + 2) * 100;
        double player2WinRate = (double) 3 / (3 + 4) * 100;
        double player3WinRate = (double) 1 / (1 + 6) * 100;

        assertEquals(player1WinRate, 71.43, 0.01);
        assertEquals(player2WinRate, 42.86, 0.01);
        assertEquals(player3WinRate, 14.29, 0.01);
    }
    
    // Test that the overall results are printed correctly
    @Test
    public void testPrintOverallResults() {

        ourplayer1.setPlayer(player1);
        ourplayer2.setPlayer(player2);

        harness.addPlayer(ourplayer1);
        harness.addPlayer(ourplayer2);

        harness.getTotalWins().put(ourplayer1, 3);
        harness.getTotalWins().put(ourplayer2, 2);
        harness.getTotalLosses().put(ourplayer1, 2);
        harness.getTotalLosses().put(ourplayer2, 3);
        harness.getTotalAp().put(ourplayer1, 50);
        harness.getTotalAp().put(ourplayer2, 40);

        // Capture console output
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outputStream));

        harness.printOverallResults();

        String output = outputStream.toString();
        assertTrue(output.contains("Overall Results After All Matchups:"));
        assertTrue(output.contains("Player 1 - Wins: 3, Losses: 2, Win Rate: 60.00%, Avg AP: 10.00"));
        assertTrue(output.contains("Player 2 - Wins: 2, Losses: 3, Win Rate: 40.00%, Avg AP: 8.00"));
    }
}
