# Scores! 🚀⚽

Final project for AUEB Coding Factory.

![AUEB Logo](images/logo.png)

Management and results monitoring application for local FIFA tournaments. Useful for both players and tournament administrators. Simple, intuitive, with admin features.

<!-- #################### TECHNOLOGIES #################### -->
## Technologies 🔧

* Java - Spring Boot
* Thymeleaf template engine
* MySQL database


<!-- #################### USAGE #################### -->
## Usage 👤

<!-- Home screen -->
### Home screen

Landing page with 3-button choice menu between players, teams, and results.

![Home screen](./images/home.png)

<!-- Log in -->
### Log in
![Login](./images/login.png)

<!-- Players -->
### Players

##### Player list

All players of the tournament with pagination. Each player's name is clickable. Each players has an associated name and picture. 
![player List](./images/playerListUser.png)

##### Selected player

View results regarding a player. Can be accessed either by a dedicated button with a dropdown list or by clicking on a player.
![select player](./images/selectPlayer.png)
![selected player](./images/playerSelected.png)

##### Player stats

View meta-information regarding a certain player, like their win rate.
![player statistics](./images/playerStatistics.png)

##### Ranking

Player ranking in the tournament. Win = 3 points, Loss = 0 points, Draw = 1 point.
![ranking](./images/ranking.png)

<!-- Teams -->
### Teams

##### Team list

All teams picked by players. Each team name is clickable.
![team list](./images/teamsListUser.png)

##### Selected team

Team summary. Information, titles.Can be accessed either by a dedicated button with a dropdown list or by clicking on a team.
![select team](./images/selectTeam.png)
![selected team](./images/selectedTeam.png)

<!-- Results -->
### Results

##### Results list

What every player wants to see! All games in a list and their results.
![results list](./images/resultListUser.png)

##### Selected result

A page with a single result, mostly to be used for social media screenshots! Can be accessed either by a dedicated button with a dropdown list or by clicking on a result ID.
![select result](./images/selectResult.png)
![selected result](./images/selectedResult.png)




<!-- #################### ADMIN USAGE #################### -->
## Admin usage 🛡️

Selected actions can only be performed by administrators, i.e., the tournament managers. These actions include

### Player add/update/delete

* Add

    ![add Player button](./images/playerListAdmin.png)
    ![add player](./images/addPlayer.png)

* Update
    ![update player button1](./images/playerSelectedAdmin.png)
    ![update player button2](./images/playerStatisticsAdmin.png)
    ![update player](./images/updatePlayer.png)

* Delete (with confirmation)
    ![delete player](./images/deletePlayer.png)

### Team add/update/delete

* Add
    ![add team button](./images/teamListAdmin.png)
    ![add team](./images/addTeam.png)

* Update
     ![update team button](./images/selectedTeamAdmin.png)
     ![update team](./images/updateTeam.png)

* Delete (with confirmation)
    ![delete team](./images/deleteTeam.png)

### Results add/update/delete/auditing

* Add / Auditing
    ![results list](./images/resultListAdmin.png)
    ![add results](./images/addResult.png)
* Update
     ![update result button](./images/selectedResultAdmin.png)
     ![update result](./images/updateResult.png)

* Delete (with confirmation)
     ![delete result](./images/deleteResult.png)


<!-- #################### MISC SCREENS #################### -->
## Misc screens 💡

##### No players/teams/results screen

What is shown to the user/admin when no players/teams/results are in the database.
The message change for each entity. (in the image, its for players.)
* User
    ![empty players](./images/emptyPlayersError.png)
* Admin
     ![empty players admin](./images/emptyPlayersErrorAdmin.png)

##### 404

What is shown when someone is trying to be smart but fails.
![404](./images/error404.png)

##### Insertion failed

Not sure how you got to this screen. But something went really wrong!
![Insert Error](./images/InsertError.png)

<!-- #################### DEVELOPMENT GUIDE #################### -->
## Development guide 👨‍💻

Development took place using Intellij and Java 11.

Website can be accessed at `localhost:8080`

### Database initialization

Tested with MySQL 8.0.35.

```
CREATE TABLE players (
    player_id INT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(255) NOT NULL,
    player_photo LONGBLOB
);

CREATE TABLE teams (
    team_id INT AUTO_INCREMENT PRIMARY KEY,
    team_name VARCHAR(255) NOT NULL,
    team_photo LONGBLOB,
    summary  VARCHAR(1500)
);

CREATE TABLE gameresults (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    gamedate DATE,
    player1_id INT,
    player2_id INT,
    team1_id INT,
    team2_id INT,
    score_player1 INT,
    score_player2 INT,
    winner_player_id INT,
    loser_player_id INT,
    winning_team_id INT,
    losing_team_id INT,
    FOREIGN KEY (player1_id) REFERENCES players(player_id),
    FOREIGN KEY (player2_id) REFERENCES players(player_id),
    FOREIGN KEY (team1_id) REFERENCES teams(team_id),
    FOREIGN KEY (team2_id) REFERENCES teams(team_id),
    FOREIGN KEY (winner_player_id) REFERENCES players(player_id),
    FOREIGN KEY (loser_player_id) REFERENCES players(player_id),
    FOREIGN KEY (winning_team_id) REFERENCES teams(team_id),
    FOREIGN KEY (losing_team_id) REFERENCES teams(team_id)
);
```

### Admin accounts

Declare in `SecurityConfig.java`

