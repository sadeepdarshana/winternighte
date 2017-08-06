package com.example.sadeep.winternightd.controllers;

/**
 * Created by Sadeep on 10/12/2016.
 */
final public class ToolbarController {

    private ToolbarController(){}//ToolbarController cannot be instantiated



    /**
     * the status (on or off) of the 4 toolbar buttons bold, italic, underline and highlight.
     * (the order has been purposely made to coincide with the relevant span type [@see SpansFactory])
     * @see com.example.sadeep.winternightd.factories.SpansFactory */
    public final static boolean[] BIUH =new boolean[] { false, false, false, false };

}
