package com.googlecode.gtalksms.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import android.util.Log;

import com.googlecode.gtalksms.MainService;
import com.googlecode.gtalksms.R;
import com.googlecode.gtalksms.tools.StringFmt;
import com.googlecode.gtalksms.tools.Tools;
import com.googlecode.gtalksms.xmpp.XmppMsg;

public class HelpCmd extends CommandHandlerBase {
    private static XmppMsg _msg;  // brief help message
    private static XmppMsg _msgAll;   // full help message    
    private Map<String, CommandHandlerBase> commands;
    
    private static XmppMsg _msgContact;
    private static XmppMsg _msgMessage;
    private static XmppMsg _msgGeo;
    private static XmppMsg _msgSystem;
    private static XmppMsg _msgCopy;
    private static XmppMsg _msgMedia;
    private static XmppMsg _msgInternal; // Dev or Expert users
    
    private static XmppMsg _msgCategories = new XmppMsg();
    
    private String format(int resHelp, Object... objects ) {
        String [] keys = new String[objects.length];
        for (int i = 0 ; i < objects.length ; ++i) {
            keys[i] = objects[i].toString();  
        }
        return "- " + StringFmt.join(keys, getString(R.string.or), true) + ": " + getString(resHelp);
    }
    
    public HelpCmd(MainService mainService) {
        super(mainService, CommandHandlerBase.TYPE_INTERNAL, "Help", new Cmd("?", "help"));
        
        _msg = new XmppMsg();
        
        ArrayList<String> allHelp = new ArrayList<String>();
        ArrayList<String> contactHelp = new ArrayList<String>();
        ArrayList<String> messageHelp = new ArrayList<String>();
        ArrayList<String> geoHelp = new ArrayList<String>();
        ArrayList<String> systemHelp = new ArrayList<String>();
        ArrayList<String> mediaHelp = new ArrayList<String>();
        ArrayList<String> copyHelp = new ArrayList<String>();
        ArrayList<String> internalHelp = new ArrayList<String>();

        String contactCmds = "";
        String messageCmds = "";
        String geoCmds = "";
        String systemCmds = "";
        String mediaCmds = "";
        String copyCmds = "";
        String internalCmds = "";
            
        commands = MainService.getActiveCommands();
        Set<CommandHandlerBase> commandSet = MainService.getActiveCommandSet();
        
        _msg.appendLine(getString(R.string.chat_help_title));
        _msg.appendLine(format(R.string.chat_help_help, "\"help\""));
        _msg.appendLine(format(R.string.chat_help_help_all, "\"help:all\""));
        _msg.appendLine(format(R.string.chat_help_help_categories, "\"help:categories\"", "\"help:cat\""));    
        _msg.appendLine("- " + makeBold("\"help:#command#\"") + " - " + makeBold("\"help:#category#\""));
        
        for (CommandHandlerBase c : commandSet) {
        	 String str = c.getCommandsAsString();
        	 ArrayList<String> helpLines = c.help();
        	       
            // do nothing if the command provides no help
            if (helpLines.isEmpty()) { 
                if (c.mCmdType == CommandHandlerBase.TYPE_INTERNAL) {
                    internalCmds += str;
                }
                continue;
            }
            
            
            allHelp.addAll(helpLines);
            
            switch (c.mCmdType) {
                case CommandHandlerBase.TYPE_CONTACTS:
                    contactCmds += str;
                    contactHelp.addAll(helpLines);
                    break;
                case CommandHandlerBase.TYPE_COPY:
                    copyCmds += str;
                    copyHelp.addAll(helpLines);
                    break;
                case CommandHandlerBase.TYPE_GEO:
                    geoCmds += str;
                    geoHelp.addAll(helpLines);
                    break;
                case CommandHandlerBase.TYPE_MESSAGE:
                    messageCmds += str;
                    messageHelp.addAll(helpLines);
                    break;
                case CommandHandlerBase.TYPE_SYSTEM:
                    systemCmds += str;
                    systemHelp.addAll(helpLines);
                    break;
                case CommandHandlerBase.TYPE_MEDIA:
                    mediaCmds += str;
                    mediaHelp.addAll(helpLines);
                    break;
                case CommandHandlerBase.TYPE_INTERNAL:
                    internalCmds += str;
                    internalHelp.addAll(helpLines);
                    break;
                default:
                    Log.w(Tools.LOG_TAG, "help command unkown command type");           
            }
        }
        
        _msgAll = new XmppMsg(getString(R.string.chat_help_title), true);
        _msgContact = new XmppMsg(getString(R.string.chat_help_title), true);
        _msgMessage = new XmppMsg(getString(R.string.chat_help_title), true);
        _msgGeo = new XmppMsg(getString(R.string.chat_help_title), true);
        _msgSystem = new XmppMsg(getString(R.string.chat_help_title), true);
        _msgCopy = new XmppMsg(getString(R.string.chat_help_title), true);
        _msgMedia = new XmppMsg(getString(R.string.chat_help_title), true);
        _msgInternal = new XmppMsg(getString(R.string.chat_help_title), true);
        _msgCategories = new XmppMsg(getString(R.string.chat_help_title), true);
        
        addLinesToMsg(_msgAll, allHelp);
        addLinesToMsg(_msgContact, contactHelp);
        addLinesToMsg(_msgMessage, messageHelp);
        addLinesToMsg(_msgGeo, geoHelp);
        addLinesToMsg(_msgSystem, systemHelp);
        addLinesToMsg(_msgCopy, copyHelp);
        addLinesToMsg(_msgMedia, mediaHelp);
        addLinesToMsg(_msgInternal, internalHelp);
        
        _msgInternal.appendLine(makeBold("Internal commands") + ": " + StringFmt.delLastChar(internalCmds, 2));
        _msgCategories.appendLine("- " + makeBold("\"help:contacts\"") + ": " + StringFmt.delLastChar(contactCmds, 2));
        _msgCategories.appendLine("- " + makeBold("\"help:text\"") + ": " + StringFmt.delLastChar(copyCmds, 2));               
        _msgCategories.appendLine("- " + makeBold("\"help:geo\"") + ": " + StringFmt.delLastChar(geoCmds, 2));
        _msgCategories.appendLine("- " + makeBold("\"help:media\"") + ": " + StringFmt.delLastChar(mediaCmds, 2));
        _msgCategories.appendLine("- " + makeBold("\"help:message\"") + ": " + StringFmt.delLastChar(messageCmds, 2));
        _msgCategories.appendLine("- " + makeBold("\"help:system\"") + ": " + StringFmt.delLastChar(systemCmds, 2));
    }

    @Override
    protected void execute(String cmd, String args) {
        if (args.equals("all")) {
            send(_msgAll);
        } else if (commands.containsKey(args)) {
        	ArrayList<String> helpLines = commands.get(args).help();
            if (!helpLines.isEmpty()) {
                XmppMsg helpMsg = new XmppMsg();
                addLinesToMsg(helpMsg, helpLines);
                send(helpMsg);
            }
        } else if (args.equals("contacts")) {
            send(_msgContact);
        } else if (args.equals("message")) {
            send(_msgMessage);
        } else if (args.equals("geo")) {
            send(_msgGeo);
        } else if (args.equals("system")) {
            send(_msgSystem);
        } else if (args.equals("media")) {
            send(_msgMedia);
        } else if (args.equals("text")) {
            send(_msgCopy);
        } else if (args.equals("internal")) {
            send(_msgInternal);
        } else if (args.equals("cat") || args.equals("categories")) {
            send(_msgCategories);
        } else {
            send(_msg);
        }
    }
    
    /**
     * Adds lines to the XmppMsg, one per line
     * does nothing if lines is null
     * 
     * @param msg
     * @param lines - can be null
     */
    private static final void addLinesToMsg(XmppMsg msg, ArrayList<String> lines) {
        if (lines == null) {
        	return;
        }
        
        Collections.sort(lines);
        
        for (String line : lines) {
            msg.appendLine(line);
        }
    }

    @Override
    protected void initializeSubCommands() {
    }
}
