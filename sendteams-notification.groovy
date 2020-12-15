def call () {
    String jenkinsConnectorURL = "https://outlook.office.com/webhook/0bb6cc06-b674-4e70-9e36-b6796abf865e@15ccb6d1-d335-4996-b6f9-7b6925f08121/IncomingWebhook/0daf75e936e5453a9ffdd623cda28e6a/e1ac9b8d-dfba-4ac4-8476-75359540e61f"
    def connectionWithTeams = new URL(jenkinsConnectorURL).openConnection();
    
    String jobName = "${env.JOB_NAME}"
    // String branchName = "${env.}"
    String buildNumber = "${env.BUILD_NUMBER}"
    // String buildStatus =
    String buildURL = "${env.RUN_DISPLAY_URL}"
    
    String triggeredBy = ""
    
    sh '''
        env
    '''
    
    connectionWithTeams.setRequestMethod("POST")
    connectionWithTeams.setDoOutput(true)
    connectionWithTeams.setRequestProperty("Content-Type", "application/json")
        
    def notificationInJSON = """
        {
            "type": "message",
            "attachments": [
                {
                    "contentType": "application/vnd.microsoft.card.adaptive",
                    "contentUrl": null,
                    "content": {
                        "\$schema": "http://adaptivecards.io/schemas/adaptive-card.json",
                        "type": "AdaptiveCard",
                        "version": "1.2",
                        "body": [
                            {
                                "type": "Container",
                                "id": "fbcee869-2754-287d-bb37-145a4ccd750b",
                                "padding": "Default",
                                "spacing": "None",
                                "items": [
                                    {
                                        "type": "Container",
                                        "id": "1e6754b2-fdb8-c5c3-7448-f551067b1264",
                                        "padding": "None",
                                        "items": [
                                            {
                                                "type": "TextBlock",
                                                "id": "c4ffbc72-512a-8944-0fd9-1e3feae2af26",
                                                "text": "Notification from ${jobName}",
                                                "wrap": true,
                                                "isSubtle": true
                                            }
                                        ]
                                    },
                                    {
                                        "type": "TextBlock",
                                        "id": "374adce6-0219-0000-9d66-a8e8a6ebed2a",
                                        "text": "digital-expert-service | develop | #${buildNumber}",
                                        "wrap": true,
                                        "size": "Medium",
                                        "weight": "Bolder"
                                    },
                                    {
                                        "type": "TextBlock",
                                        "id": "44906797-222f-9fe2-0b7a-e3ee21c6e380",
                                        "text": "Build Un-Successful ☹",
                                        "wrap": true,
                                        "weight": "Bolder",
                                        "size": "Large",
                                        "color": "Attention"
                                    },
                                    {
                                        "type": "ColumnSet",
                                        "id": "5032d2c4-03f7-3528-f9d7-92aaad0919aa",
                                        "columns": [
                                            {
                                                "type": "Column",
                                                "id": "966de84e-4c64-35ff-b573-627ffe481c09",
                                                "padding": "None",
                                                "width": "auto",
                                                "items": [
                                                    {
                                                        "type": "TextBlock",
                                                        "id": "ab191039-eae5-8b3d-95c9-07ca31cc05cb",
                                                        "text": "Triggered By - ",
                                                        "wrap": true,
                                                        "isSubtle": true,
                                                        "weight": "Lighter"
                                                    }
                                                ]
                                            },
                                            {
                                                "type": "Column",
                                                "id": "e7f6df00-b340-96e2-76b4-0ffe543dd0dc",
                                                "padding": "None",
                                                "width": "auto",
                                                "items": [
                                                    {
                                                        "type": "TextBlock",
                                                        "id": "21fdb1ed-46a0-75d2-5c1a-c7f84bb18231",
                                                        "text": "${triggeredBy}",
                                                        "wrap": true
                                                    }
                                                ],
                                                "spacing": "Small"
                                            }
                                        ],
                                        "padding": "None"
                                    },
                                    {
                                        "type": "Container",
                                        "id": "0a960e03-0a9a-3b7e-8913-49fa84fb3a7d",
                                        "padding": "None",
                                        "items": [
                                            {
                                                "type": "ActionSet",
                                                "id": "c8a5ba7e-7ec6-53ae-79da-0cfb952a527e",
                                                "actions": [
                                                    {
                                                        "type": "Action.OpenUrl",
                                                        "id": "0893997a-9ca7-fdc4-5567-d272bcbe1cc9",
                                                        "title": "View Build in Jenkins",
                                                        "url": "${buildURL}",
                                                        "style": "positive",
                                                        "isPrimary": true
                                                    },
                                                    {
                                                        "type": "Action.OpenUrl",
                                                        "id": "a92769aa-5108-cf34-c0c4-04b1135443bb",
                                                        "title": "Console Log",
                                                        "url": "https://amdesigner.azurewebsites.net"
                                                    }
                                                ],
                                                "separator": true,
                                                "spacing": "Medium"
                                            }
                                        ]
                                    }
                                ]
                            }
                        ],
                        "padding": "None"
                    }
                }
            ]
        }
    """
    
    connectionWithTeams.getOutputStream().write(notificationInJSON.getBytes("UTF-8"));
    def responseCodeReceived = connectionWithTeams.getResponseCode();
    println(responseCodeReceived);
    if(responseCodeReceived.equals(200)) {
        println(connectionWithTeams.getInputStream().getText());
    }
}
