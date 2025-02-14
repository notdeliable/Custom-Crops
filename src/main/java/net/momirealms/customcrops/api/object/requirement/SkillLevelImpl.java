/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.api.object.requirement;

import net.momirealms.customcrops.CustomCrops;
import net.momirealms.customcrops.api.object.action.Action;
import net.momirealms.customcrops.integration.SkillInterface;
import org.jetbrains.annotations.Nullable;

public class SkillLevelImpl extends AbstractRequirement implements Requirement {

    private final int level;

    public SkillLevelImpl(@Nullable String[] msg, @Nullable Action[] actions, int level) {
        super(msg, actions);
        this.level = level;
    }

    @Override
    public boolean isConditionMet(CurrentState currentState) {
         SkillInterface skillInterface = CustomCrops.getInstance().getIntegrationManager().getSkillInterface();
         if (skillInterface == null || currentState.getPlayer() == null) return true;
         if (skillInterface.getLevel(currentState.getPlayer()) >= level) {
             return true;
         }
         notMetMessage(currentState);
         return false;
    }
}
