/* (C)2022 */
package com.example.turnierplaner.tournament.leagueSystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import com.example.turnierplaner.tournament.Participant
import com.example.turnierplaner.tournament.Tournament
import com.example.turnierplaner.tournament.leagueSystem.schedule.ListResult
import com.example.turnierplaner.tournament.leagueSystem.schedule.Result

var roundNumber = 1
var listResult: ListResult? = null
var rememberTournamentRound = ""
var change = false
var tournament: Tournament? = null
var roundNumberInList = 0

/** Composable method who shows the schedule of the tournament */
@Composable
fun ScheduleComposable(navController: NavHostController, tournamentName: String?) {
  setTourn(findTournament(tournamentName))
  actualizeTournamentSchedule(getTournament(tournamentName!!)!!)
  // setListRes(tournamentName!!)
  var expanded by remember { mutableStateOf(false) }
  var textFieldSize by remember { mutableStateOf(Size.Zero) }
  val suggestions = mutableListOf<String>()
  var selectedTournamentRound by remember { mutableStateOf("") }
  if (rememberTournamentRound != "") {
    selectedTournamentRound = rememberTournamentRound
  }
  val numberOfRounds =
      (getRow(getNumberOfActualParticipants(getTournament(tournamentName)!!.participants)) * 2) - 1

  for (i in 0 until numberOfRounds) {
    suggestions.add(i, "round: ${i + 1}")
  }

  Scaffold(
      topBar = {
        Column(modifier = Modifier.fillMaxWidth()) {
          TopAppBar(
              backgroundColor = Color.White,
              elevation = 1.dp,
              title = {
                Text(text = "Tournament schedule: ${getTournament(tournamentName)?.name}")
              },
              actions = {
                IconButton(
                    onClick = {
                      navController.navigate(
                          "single_tournament_route/${getTournament(tournamentName)?.name}")
                    },
                ) {
                  Icon(
                      imageVector = Icons.Rounded.ArrowBack,
                      contentDescription = "Button to go back to SingleTournamentScreen",
                  )
                }
              })
        }
      },
      content = {

        // Tournament type
        val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
        val actualRound = methodWhichRound(getTournament(tournamentName)!!)
        Column() {
          Column() {
            // DropDownMenu for the tournament rounds
            OutlinedTextField(
                value = selectedTournamentRound,
                readOnly = true,
                modifier =
                    Modifier.fillMaxWidth().padding(5.dp).onGloballyPositioned { coordinates ->
                      // This value is used to assign to the DropDown the same width
                      textFieldSize = coordinates.size.toSize()
                    },
                onValueChange = { selectedTournamentRound = it },
                label = { Text("Actual round $actualRound") },
                leadingIcon = {
                  IconButton(onClick = { /*TODO*/}) {
                    Icon(
                        imageVector = Icons.Filled.FormatListNumbered,
                        contentDescription = "TournamentList")
                  }
                },
                trailingIcon = { Icon(icon, "Arrow", Modifier.clickable { expanded = !expanded }) })
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier =
                    Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
            ) {
              suggestions.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                      selectedTournamentRound = label
                      rememberTournamentRound = selectedTournamentRound
                      expanded = false
                      val splitList = selectedTournamentRound.split(" ")
                      roundNumber = splitList[1].toInt()
                      roundNumberInList =
                          if (roundNumber > 0) {
                            roundNumber - 1
                          } else {
                            0
                          }
                      // gameListRound = listResult!!.allGames.get(roundNumber)
                    }) { Text(text = label) }
              }
            }
          }

          // set cell Width of the table
          val cellWidth: (Int) -> Dp = { index ->
            when (index) {
              0 -> 60.dp
              2 -> 80.dp
              else -> 125.dp
            }
          }
          // set title of the columns
          val headerCellTitle: @Composable (Int) -> Unit = { index ->
            val value =
                when (index) {
                  0 -> "Nr."
                  1 -> "Part. 1"
                  2 -> "Result"
                  3 -> "Part. 2"
                  else -> ""
                }
            // define text specs
            Text(
                text = value,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(5.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Black,
                textDecoration = TextDecoration.Underline)
          }

          val cellText: @Composable (Int, Result) -> Unit = { index, match ->
            val value =
                when (index) {
                  0 ->
                      (getTournament(tournamentName)!!.schedule!![roundNumberInList].indexOf(
                              match) + 1)
                          .toString()
                  1 -> match.participant1.name
                  2 -> "${match.resultParticipant1} : ${match.resultParticipant2}"
                  3 -> match.participant2.name
                  else -> ""
                }

            Text(
                text = value,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
          }

          Table(
              columnCount = 4,
              cellWidth = cellWidth,
              data = getTournament(tournamentName)!!.schedule!![roundNumber - 1],
              modifier = Modifier.verticalScroll(rememberScrollState()),
              headerCellContent = headerCellTitle,
              cellContent = cellText)

          Column(
              modifier = Modifier.fillMaxSize().padding(24.dp),
              verticalArrangement = Arrangement.spacedBy(18.dp),
              horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                  navController.navigate(
                      "pointsResult_route/${getTournament(tournamentName)?.name}")
                },
                enabled = true,
                // border = BorderStroke( width = 1.dp, brush = SolidColor(Color.Blue)),
                shape = MaterialTheme.shapes.medium) {
              Text(text = "Add or Change the game result", color = Color.White)
            }
            /*Button(
                onClick = { navController.navigate("changeResult_route/${tourneyT.name}")},
                enabled = true,
                shape = MaterialTheme.shapes.medium
            ){
                Text(text = "Change the game result")
            }*/
          }
        }
      })
}

/** creates a game schedule for the first round */
fun createSchedule(
    participants: MutableList<Participant>,
    numberOfActualParticipant: Int
): MutableList<Result> {
  val list = participants
  val row = getRow(numberOfActualParticipant)
  val matrix = MutableList(row) { Result() }
  var count = 0
  for (i in 0 until row) {
    matrix[i].participant1 = list[count]
    if (count + 2 != numberOfActualParticipant + 1) {
      matrix[i].participant2 = list[count + 1]
    }
    count += 2
  }
  return matrix
}

fun actualizeTournamentSchedule(tourney1: Tournament): MutableList<MutableList<Result>> {
  val oldSchedule = tourney1.schedule
  // listCopy(tourney1)
  val participantList = tourney1.participants
  val scheduleNew = mutableListOf<MutableList<Result>>()
  val roundnumber2 = (getRow(getNumberOfActualParticipants(participantList)) * 2) - 1
  scheduleNew.add(createSchedule(participantList, getNumberOfActualParticipants(participantList)))
  for (i in 2..roundnumber2) {
    scheduleNew.add(
        changeOpponent1(scheduleNew[0], getRow(getNumberOfActualParticipants(participantList)), i))
  }
  if (oldSchedule != null) {
    for (i in oldSchedule) {
      for (j in i) {
        for (k in scheduleNew) {
          for (z in k) {
            if ((j.participant1 == z.participant1) && (j.participant2 == z.participant2)) {
              z.resultParticipant1 = j.resultParticipant1
              z.resultParticipant2 = j.resultParticipant2
            }
          }
        }
      }
    }
  }
  tourney1.schedule = scheduleNew
  return tourney1.schedule!!
}

/** change the game opponents for roundNumber - 2 rounds rotate the list */
fun changeOpponent1(list: MutableList<Result>, row: Int, roundNumber: Int): MutableList<Result> {
  var list1 = list
  for (k in 2..roundNumber) {
    val listNewRound = MutableList(row) { Result() }
    for (i in 0 until row) {
      if (i == 0) {
        listNewRound[i].participant1 = list1[i].participant1
        listNewRound[i + 1].participant1 = list1[i].participant2
      } else if (i == row - 1) {
        listNewRound[i].participant2 = list1[i].participant1
        listNewRound[i - 1].participant2 = list1[i].participant2
      } else {
        listNewRound[i + 1].participant1 = list1[i].participant1
        listNewRound[i - 1].participant2 = list1[i].participant2
      }
    }
    list1 = listNewRound
  }
  return list1
}

/** return the NumberOFGames pro round / rowNumber */
fun getRow(numberOfActualParticipants: Int): Int {
  val row =
      if ((numberOfActualParticipants % 2) == 1) {
        (numberOfActualParticipants / 2) + 1
      } else {
        (numberOfActualParticipants / 2)
      }
  return row
}

/** method who splits a string */
fun splitString(games: String, index: Int): String {
  val split = games.split(" vs. ")
  return split.get(index)
}

/** return the Number of actual Participants */
fun getNumberOfActualParticipants(participants: MutableList<Participant>): Int {
  var count = 0
  for (i in participants) {
    if (i.name != "") {
      count++
    }
  }
  return count
}

fun createScheduleTournament(
    participants: MutableList<Participant>
): MutableList<MutableList<Result>> {
  val allGames = mutableListOf<MutableList<Result>>()
  val roundNumber = 0
  allGames.add(createSchedule(participants, 0))
  if (roundNumber >= 2) {
    for (i in 2..roundNumber) {
      allGames.add(changeOpponent1(allGames[0], getRow(roundNumber + 1), i))
    }
  }
  return allGames
}

/** fill the mutableList with games */
fun fillGameString(tourney: Tournament): MutableList<String> {
  val suggestionsGame = mutableListOf<String>()
  for (i in 0 until tourney.schedule!![roundNumber - 1].size) {
    val k = tourney.schedule!![roundNumber - 1][i].participant1.name
    val dummy = tourney.schedule!![roundNumber - 1][i].participant2.name
    suggestionsGame.add(i, "$k vs. $dummy")
  }
  return suggestionsGame
}

/** return the actual round */
fun methodWhichRound(tourney: Tournament): Int {
  var round = 0
  for (i in tourney.schedule!!) {
    for (j in i) {
      if ((j.resultParticipant1 == "") && (j.resultParticipant2 == "")) {
        round = tourney.schedule!!.indexOf(i)
        return round + 1
      }
    }
  }
  return round + 1
}

/** return the gameresult */
fun getGameResult(game: String, tourney: Tournament): Result {
  val splitString = game.split(" vs. ")
  val participant1 = splitString[0]
  val participant2 = splitString[1]
  var result = Result()
  for (i in tourney.schedule!!) {
    for (j in i) {
      if (j.participant1.name.equals(participant1) && j.participant2.name.equals(participant2)) {
        result = j
        break
      }
    }
  }
  return result
}

fun getTournament(tourneyName: String): Tournament? {
  if (tournament == null) {
    tournament = findTournament(tourneyName)
  }
  return tournament
}

fun setTourn(tourney: Tournament) {
  tournament = tourney
}

fun getListRes(): ListResult? {
  return listResult
}

fun setListRes(tournamentName: String) {
  var tourney = getTournament(tournamentName)
  listResult = ListResult(tourney!!.participants)
}