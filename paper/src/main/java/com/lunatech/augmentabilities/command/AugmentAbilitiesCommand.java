package com.lunatech.augmentabilities.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import com.lunatech.augmentabilities.AbstractAugmentAbilities;
import com.lunatech.augmentabilities.AugmentAbilities;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.wordweaver.Translation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.lunatech.augmentabilities.command.CommandHandler.BASE_PERM;

/**
 * Class containing the code for the main command.
 */
final class AugmentAbilitiesCommand extends Command {
    private final AbstractAugmentAbilities plugin;

    /**
     * Instantiates and registers a new command.
     */
    AugmentAbilitiesCommand(AbstractAugmentAbilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand("augmentabilities")
            .withAliases("augment", "augments")
            .withHelp("Open the augments interface.", "Open the augments interface.")
            .withPermission(BASE_PERM)
            .withSubcommands(
                commandMenu(),
                commandRoll(),
                commandAdmin(),
                new TranslationCommand().command(),
                new DumpCommand().command()
            )
            .executesPlayer(this::executorMenu);
    }

    private CommandAPICommand commandMenu() {
        return new CommandAPICommand("menu")
            .withHelp("Open the augment management menu.", "Open the augment management menu.")
            .withPermission(BASE_PERM + ".menu")
            .executesPlayer(this::executorMenu);
    }

    private CommandAPICommand commandRoll() {
        return new CommandAPICommand("roll")
            .withHelp("Open the augment roll choice menu.", "Open the augment roll choice menu.")
            .withPermission(BASE_PERM + ".roll")
            .executesPlayer((player, args) -> {
                ((AugmentAbilities) plugin).getAugmentService().triggerRollMenu(player);
            });
    }

    private CommandAPICommand commandAdmin() {
        return new CommandAPICommand("admin")
            .withHelp("Admin controls for augments.", "Admin controls for augments.")
            .withPermission(BASE_PERM + ".admin")
            .withSubcommands(
                 new CommandAPICommand("give")
                    .withArguments(new EntitySelectorArgument.OnePlayer("target"))
                    .withArguments(new IntegerArgument("rolls", 1, 100))
                    .executes((sender, args) -> {
                        Player target = (Player) args.get("target");
                        Integer rolls = (Integer) args.get("rolls");
                        if (target != null && rolls != null) {
                            PlayerAugmentProfile profile = ((AugmentAbilities) plugin).getAugmentService().getProfile(target.getUniqueId());
                            profile.setPendingRolls(profile.getPendingRolls() + rolls);
                            ((AugmentAbilities) plugin).getAugmentService().saveProfileAsync(target.getUniqueId());
                            
                            sender.sendMessage(ColorParser.of(Translation.of("commands-augment.give-success"))
                                .with("rolls", String.valueOf(rolls))
                                .with("player", target.getName())
                                .build());
                            
                            target.sendMessage(ColorParser.of(Translation.of("commands-augment.received-rolls"))
                                .with("rolls", String.valueOf(rolls))
                                .build());
                        }
                    }),
                new CommandAPICommand("clear")
                    .withArguments(new EntitySelectorArgument.OnePlayer("target"))
                    .executes((sender, args) -> {
                        Player target = (Player) args.get("target");
                        if (target != null) {
                            PlayerAugmentProfile profile = ((AugmentAbilities) plugin).getAugmentService().getProfile(target.getUniqueId());
                            profile.clearAugments();
                            ((AugmentAbilities) plugin).getAugmentService().saveProfileAsync(target.getUniqueId());
                            
                            sender.sendMessage(ColorParser.of(Translation.of("commands-augment.clear-success"))
                                .with("player", target.getName())
                                .build());
                            
                            target.sendMessage(ColorParser.of(Translation.of("commands-augment.cleared-by-admin"))
                                .build());
                        }
                    })
            );
    }

    private void executorMenu(Player player, CommandArguments args) {
        ((AugmentAbilities) plugin).getAugmentService().openAugmentMenu(player);
    }
}
