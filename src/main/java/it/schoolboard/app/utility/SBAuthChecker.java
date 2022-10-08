/*
    SchoolBoard: the free and open source all-in-one platform for school
    Copyright (C) 2020-2022 Lo Mele Vittorio
    Copyright (C) 2022 Flowopia Network

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

    INFO:           info@schoolboard.it
    COMMERCIAL:     commerciale@schoolboard.it
    DEVELOPEMENT:   sviluppo@schoolboard.it
 */

package it.schoolboard.app.utility;

import org.springframework.security.core.context.SecurityContext;

public class SBAuthChecker {

    public static boolean checkRole(SecurityContext securityContext) {
        boolean flag = false;
        for (Object role : securityContext.getAuthentication().getAuthorities().toArray()) {
            if (role.toString().equals("ROLE_schoolboard")) {
                flag = true;
            }
        }
        return flag;
    }

}
