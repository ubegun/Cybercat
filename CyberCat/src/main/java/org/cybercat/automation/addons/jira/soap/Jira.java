package org.cybercat.automation.addons.jira.soap;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.core.AutomationMain;

public class Jira {

    private static Logger log = LogManager.getLogger(Jira.class);
    
    JiraSoapService jiraService;
    String authToken;
    private String jiraEndPoint;
    private String login; // TODO write login/pass
    private String password;
    private static Jira instance;
    private String jiraUrl;
    
    enum EBugStatus{
        Open("1"),
        InProgress("3"),//
        ReOpened("4"),//
        Resolved("5"),//
        Closed("6");
        
        private String value;
        
        public String getValue() {
            return value;
        }
        
        public void setValue(String value) {
            this.value = value;
        }
        
        public boolean equals(String actValue){
            return value.equals(actValue);
        }
        
        EBugStatus(String str){
            value=str;
        }
    }
    
    enum EBugResolution{
        Unresolved(null),
        Fixed("1");
        
        private String value; 
        
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        EBugResolution(String str){
            value = str;
        }
        public boolean equals(String actValue){
            return value.equals(actValue);
        }
    }
    
    
    
    public Jira() throws AutomationFrameworkException {
        JiraSoapServiceServiceLocator soapLocator = new JiraSoapServiceServiceLocator(jiraEndPoint);
        AutomationMain mainFactory = AutomationMain.getMainFactory();
        
        jiraEndPoint = mainFactory.getProperty("jira.soap.wsdl.url");
        login = mainFactory.getProperty("jira.soap.endpoint"); 
        password = mainFactory.getProperty("jira.login");
        jiraUrl = mainFactory.getProperty("jira.password");

        JiraSoapService soapService = null;
        try {
            soapService = soapLocator.getJirasoapserviceV2(new URL(jiraUrl));
        } catch (MalformedURLException | ServiceException e) {
            e.printStackTrace();
        }
        this.jiraService = soapService;
        authorize();
        
        log.info("Jira service creation: success");
    }
    
    public static Jira getInstance() throws AutomationFrameworkException{
        if (instance==null){
            instance = new Jira();
        }
        return instance;
    }
    
    
    private boolean authorize(){
        try{
            authToken = jiraService.login(login, password);
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        log.info("Jira authorization: success");
        return !authToken.isEmpty();
    }

    public RemoteIssue getIssue(String issueID) throws RemotePermissionException, RemoteAuthenticationException, RemoteException, java.rmi.RemoteException{
        return jiraService.getIssue(authToken, issueID);
    }
    
    public static Boolean isFixed(String issueID){
        boolean fixed = false;
        String description = "";
        try{
            RemoteIssue issue = getInstance().getIssue(issueID);
            String strStatus = issue.getStatus(); 
            String strResolution =  issue.getResolution();
            description = issue.getSummary();
            
            fixed = (EBugStatus.Closed.equals(strStatus)&&EBugResolution.Fixed.equals(strResolution))||
                    (EBugStatus.Resolved.equals(strStatus)&&EBugResolution.Fixed.equals(strResolution));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        log.info("Issue #"+issueID+"["+(fixed?EBugResolution.Fixed:EBugResolution.Unresolved)+"]"+" - "+description);
        
        return fixed;
    }

    public static String getBugSummary(String bugId) throws AutomationFrameworkException {
        RemoteIssue issue;
        try {
                issue = getInstance().getIssue(bugId);
                return issue.getSummary();
        } catch (RemoteException e) {
            throw new AutomationFrameworkException(e);
        }
    }
}
