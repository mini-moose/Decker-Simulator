package main;

public enum MissionState {
  GAINING_ACCESS, // Attempting to gain access to the system
  PLAYING_THE_SYSTEM, // In the system, exploring, locating paydata and objectives, navigating defenses
  EXTRACTING_DATA, // Attempting to extract data or
  MISSION_COMPLETE,
  JACKED_OUT,
  DOWN_TIME
}
