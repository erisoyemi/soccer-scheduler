/*
*    Team Smoliv
*    Cole Briggs, Chris Axten, Erioluwa Soyemi, Grace Kelly Osena
*    CPSC 433 F24
*/

package com.scheduler.model;

/*
 * Represents a division of players in the soccer league.
 * A division is defined by a league, tier, and division number.
 */
public class Division {

    /*
     * The league of the division.
     */
    private final String league;

    /*
     * The tier of the division.
     */
    private final String tier;

    /*
     * The division number.
     */
    private final String div;

    /*
     * Constructor for the Division class.
     */
    public Division(String league, String tier, String div) {
        this.league = league;
        this.tier = tier;
        this.div = div;
    }

    /*
     * Returns the league of the division.
     */
    public String getLeague() {
        return league;
    }

    /*
     * Returns the tier of the division.
     */
    public String getTier() {
        return tier;
    }

    /*
     * Returns just the div part of the division.
     */
    public String getDiv() {
        return div;
    }

    /*
     * Returns true if the division is an evening division, false otherwise.
     */
    public boolean isEvening() {
        return div.startsWith("9");
    }

    /*
     * Compares two Division objects to see if they are referring to the same division of players.
     */
    public boolean same(Division other) {

        // Check special case for special practices
        if (getLeague().equals("CMSA") && other.getLeague().equals("CMSA")) {

            if (getTier().equals("U12T1") && other.getTier().equals("U12T1S")) return true;
            if (getTier().equals("U12T1S") && other.getTier().equals("U12T1")) return true;

            if (getTier().equals("U13T1") && other.getTier().equals("U13T1S")) return true;
            if (getTier().equals("U13T1S") && other.getTier().equals("U13T1")) return true;
        }

        // First check if league is the same
        if (!getLeague().equals(other.getLeague()))
        return false;

        // Check if tier is the same otherwise
        else if (!getTier().equals(other.getTier()))
        return false;

        // If divs don't match and neither is "all", then they are different
        else if (!getDiv().equals(other.getDiv()) && !getDiv().equals("all") && !other.getDiv().equals("all"))
        return false;

        // Otherwise, they are the same
        else
        return true;
    }
    
    /*
     * Compares two Division objects to see if they are referring to the same tier of players.
     */
    public boolean tierSame(Division other) {
        return tier.equals(other.getTier());
    }

    /*
     * Returns true if the division is a U15, U16, U17, U18, or U19 age tier.
     */
    public boolean isU15toU19() {
        return tier.startsWith("U15") || tier.startsWith("U16") || tier.startsWith("U17") || tier.startsWith("U18") || tier.startsWith("U19");
    }
}
