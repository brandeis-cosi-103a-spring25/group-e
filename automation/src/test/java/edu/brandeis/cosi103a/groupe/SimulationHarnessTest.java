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

import edu.brandeis.cosi.atg.api.Player.ScorePair;
import edu.brandeis.cosi.atg.api.PlayerViolationException;
import edu.brandeis.cosi103a.groupe.Players.BigMoneyPlayer;
import edu.brandeis.cosi103a.groupe.Players.ourPlayer;

public class SimulationHarnessTest {
    private SimulationHarness harness;
    private ourPlayer player1;
    private ourPlayer player2;
    private ourPlayer player3;

    @Before
    public void setUp() {
        harness = new SimulationHarness(5); // Simulate 5 games per matchup
        player1 = mock(ourPlayer.class);
        player2 = mock(ourPlayer.class);
        player3 = mock(ourPlayer.class);

        when(player1.getName()).thenReturn("Player 1");
        when(player2.getName()).thenReturn("Player 2");
        when(player3.getName()).thenReturn("Player 3");
    }
    
    // Test that the SimulationHarness is initialized correctly
    @Test
    public void testInitialization() {
        assertNotNull(harness);
    }
    
    // Test that players can be added to the SimulationHarness
    @Test
    public void testAddOnePlayer() {
        harness.addPlayer(player1);
        assertEquals(1, harness.getPlayers().size());
    }
    
    // Test that multiple players can be added to the SimulationHarness
    @Test
    public void testAddTwoPlayer() {
        harness.addPlayer(player1);
        harness.addPlayer(player2);

        assertEquals(2, harness.getPlayers().size());
        assertTrue(harness.getPlayers().contains(player1));
        assertTrue(harness.getPlayers().contains(player2));
    }
    
    // Test that multiple players can be added to the SimulationHarness
    @Test
    public void testAddMultiplePlayers() {
        harness.addPlayer(player1);
        harness.addPlayer(player2);
        harness.addPlayer(player3);
        assertEquals(3, harness.getPlayers().size());
    }
    
    // Test that the simulation can run with at least two players
    @Test
    public void testRunWithLessThanTwoPlayers() throws Exception {
        harness.addPlayer(player1);

        // Capture console output
        var outputStream = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outputStream));

        harness.run();

        String output = outputStream.toString();
        assertTrue(output.contains("At least two players are required to run the simulation."));
    }

    // Test that the simulateMatchup method works correctly
    @Test
    public void testSimulateMatchup() throws Exception {
    // Create a partial mock of SimulationHarness
    SimulationHarness spyHarness = org.mockito.Mockito.spy(harness);

    // Mock the GameEngine
    GameEngine mockEngine = mock(GameEngine.class);

    // Define the behavior of determineWinner
    ImmutableList<ScorePair> result1 = ImmutableList.of(new ScorePair(player1, 10), new ScorePair(player2, 5));
    ImmutableList<ScorePair> result2 = ImmutableList.of(new ScorePair(player1, 5), new ScorePair(player2, 10));
    ImmutableList<ScorePair> result3 = ImmutableList.of(new ScorePair(player1, 10), new ScorePair(player2, 5));
    ImmutableList<ScorePair> result4 = ImmutableList.of(new ScorePair(player1, 10), new ScorePair(player2, 5));
    ImmutableList<ScorePair> result5 = ImmutableList.of(new ScorePair(player1, 10), new ScorePair(player2, 5));

    when(mockEngine.determineWinner()).thenReturn(result1, result2, result3, result4, result5);

    // Override the creation of GameEngine in the simulateMatchup method
    doReturn(mockEngine).when(spyHarness).createGameEngine(player1, player2);

    // Add players to the harness
    spyHarness.addPlayer(player1);
    spyHarness.addPlayer(player2);

    // Simulate the matchup
    spyHarness.simulateMatchup(player1, player2);

    // Verify stats
    assertEquals(4, spyHarness.getTotalWins().get(player1).intValue());
    assertEquals(1, spyHarness.getTotalWins().get(player2).intValue());
    assertEquals(1, spyHarness.getTotalLosses().get(player1).intValue());
    assertEquals(4, spyHarness.getTotalLosses().get(player2).intValue());
    assertEquals(45, spyHarness.getTotalAp().get(player1).intValue()); // Total AP for player1
    assertEquals(30, spyHarness.getTotalAp().get(player2).intValue()); // Total AP for player2
    }  
    
    // Test that the overall results are calculated correctly
    @Test
    public void testOverallResults() throws Exception {
        ourPlayer player3 = new BigMoneyPlayer("Player3");
        harness.addPlayer(player1);
        harness.addPlayer(player2);
        harness.addPlayer(player3);

        // Simulate basic results
        harness.getTotalWins().put(player1, 5);
        harness.getTotalLosses().put(player1, 2);
        harness.getTotalAp().put(player1, 50);

        harness.getTotalWins().put(player2, 3);
        harness.getTotalLosses().put(player2, 4);
        harness.getTotalAp().put(player2, 30);

        harness.getTotalWins().put(player3, 1);
        harness.getTotalLosses().put(player3, 6);
        harness.getTotalAp().put(player3, 20);

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
        harness.addPlayer(player1);
        harness.addPlayer(player2);

        harness.getTotalWins().put(player1, 3);
        harness.getTotalWins().put(player2, 2);
        harness.getTotalLosses().put(player1, 2);
        harness.getTotalLosses().put(player2, 3);
        harness.getTotalAp().put(player1, 50);
        harness.getTotalAp().put(player2, 40);

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
