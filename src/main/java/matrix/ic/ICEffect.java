package matrix.ic;

import player.Player;

// Defining IC Effects so that individual ICs can override with their specific effects
public interface ICEffect {
  void applyEffect(Player player, int netHits);
  String getEffectDescription();
}
