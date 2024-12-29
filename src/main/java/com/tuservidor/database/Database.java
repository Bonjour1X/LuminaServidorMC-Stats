package com.tuservidor.database;

import com.tuservidor.teams.TeamRole;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.util.*;
import java.util.Map;
import java.util.HashMap;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import com.tuservidor.stats.ScoreType;
import org.bukkit.entity.Player;

import java.sql.Statement;

public class Database {
    private final String path;
    private Connection connection;

    public Database(String path) {
        this.path = path;
    }

    public void connect() {
        try {
            // Crear directorio si no existe
            File dataFolder = new File(path).getParentFile();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            createTables();
            System.out.println("Base de datos conectada en: " + path);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        String players = """
            CREATE TABLE IF NOT EXISTS players (
                player_id TEXT PRIMARY KEY,
                player_name TEXT,
                points INTEGER DEFAULT 0,
                kills INTEGER DEFAULT 0,
                deaths INTEGER DEFAULT 0,
                playtime INTEGER DEFAULT 0,
                blocks_mined INTEGER DEFAULT 0
            );
            """;

        String teams = """
            CREATE TABLE IF NOT EXISTS teams (
                team_id INTEGER PRIMARY KEY AUTOINCREMENT,
                team_name TEXT,
                score INTEGER DEFAULT 0,
                points INTEGER DEFAULT 0,
                total_points INTEGER DEFAULT 0
            );
            """;

        String playerTeams = """
            CREATE TABLE IF NOT EXISTS player_teams (
                player_id TEXT,
                team_id INTEGER,
                role TEXT DEFAULT 'MEMBER',
                FOREIGN KEY(player_id) REFERENCES players(player_id),
                FOREIGN KEY(team_id) REFERENCES teams(team_id)
            );
            """;

        try (var stmt = connection.createStatement()) {
            stmt.execute(players);
            stmt.execute(teams);
            stmt.execute(playerTeams);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerStats(String playerId, String playerName, int kills, int deaths) {
        String sql = """
                INSERT OR REPLACE INTO players (player_id, player_name, kills, deaths)
                VALUES (?, ?, ?, ?);
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            pstmt.setString(2, playerName);
            pstmt.setInt(3, kills);
            pstmt.setInt(4, deaths);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerKills(String playerId) {
        String sql = "SELECT kills FROM players WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("kills");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getPlayerDeaths(String playerId) {
        String sql = "SELECT deaths FROM players WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("deaths");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getPlayerTeam(String playerId) {
        String sql = """
        SELECT t.team_name 
        FROM teams t 
        JOIN player_teams pt ON t.team_id = pt.team_id 
        WHERE pt.player_id = ?
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("team_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Para bloques minados
    public int getPlayerBlocksMined(String playerId) {
        String sql = "SELECT blocks_mined FROM players WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("blocks_mined");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Para tiempo de juego
    public long getPlayerPlaytime(String playerId) {
        String sql = "SELECT playtime FROM players WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("playtime");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Para crear equipo
    public void createTeam(String teamName, String playerId) {
        String sql1 = "INSERT INTO teams (team_name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql1)) {
            pstmt.setString(1, teamName);
            pstmt.executeUpdate();

            // Obtener el ID del equipo creado
            String sql2 = "SELECT last_insert_rowid() as team_id";
            try (var stmt = connection.createStatement();
                 var rs = stmt.executeQuery(sql2)) {
                if (rs.next()) {
                    int teamId = rs.getInt("team_id");
                    // Asignar como líder
                    String sql3 = "INSERT INTO player_teams (player_id, team_id, role) VALUES (?, ?, 'LEADER')";
                    try (PreparedStatement pstmt3 = connection.prepareStatement(sql3)) {
                        pstmt3.setString(1, playerId);
                        pstmt3.setInt(2, teamId);
                        pstmt3.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Para unirse a un equipo
    public void joinTeam(String teamName, String playerId) {
        String sql = "SELECT team_id FROM teams WHERE team_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, teamName);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                int teamId = rs.getInt("team_id");
                joinTeam(teamId, playerId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void joinTeam(int teamId, String playerId) {
        String sql = "INSERT OR REPLACE INTO player_teams (player_id, team_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            pstmt.setInt(2, teamId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Para obtener info del equipo
    public String getTeamInfo(String playerId) {
        String sql = """
        SELECT t.team_name, t.score,
               (SELECT GROUP_CONCAT(p2.player_name)
                FROM players p2
                JOIN player_teams pt2 ON p2.player_id = pt2.player_id
                WHERE pt2.team_id = t.team_id) as member_names
        FROM teams t
        JOIN player_teams pt ON t.team_id = pt.team_id
        WHERE pt.player_id = ?
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return String.format("""
                %sEquipo: %s
                %sPuntos: %d
                %sMiembros: %s""",
                        ChatColor.GOLD,
                        rs.getString("team_name"),
                        ChatColor.YELLOW,
                        rs.getInt("score"),
                        ChatColor.YELLOW,
                        rs.getString("member_names").replace(",", ", "));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ChatColor.RED + "No estás en ningún equipo";
    }

    public List<Map<String, Object>> getTopPlayers(int limit) {
        String sql = """
            SELECT player_name, kills, deaths, playtime, blocks_mined
            FROM players
            ORDER BY (kills * 10 + blocks_mined - deaths * 5) DESC
            LIMIT ?
            """;
        List<Map<String, Object>> topPlayers = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> player = new HashMap<>();
                player.put("name", rs.getString("player_name"));
                player.put("score", rs.getInt("kills") * 10 + rs.getInt("blocks_mined") - rs.getInt("deaths") * 5);
                player.put("kills", rs.getInt("kills"));
                topPlayers.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topPlayers;
    }

    public List<Map<String, Object>> getTopTeams(int limit) {
        String sql = """
            SELECT t.team_name, 
                   COUNT(pt.player_id) as members,
                   SUM(p.kills) as total_kills,
                   SUM(p.blocks_mined) as total_mined
            FROM teams t
            JOIN player_teams pt ON t.team_id = pt.team_id
            JOIN players p ON pt.player_id = p.player_id
            GROUP BY t.team_id
            ORDER BY (SUM(p.kills) * 10 + SUM(p.blocks_mined) - SUM(p.deaths) * 5) DESC
            LIMIT ?
            """;
        List<Map<String, Object>> topTeams = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> team = new HashMap<>();
                team.put("name", rs.getString("team_name"));
                team.put("members", rs.getInt("members"));
                team.put("score", rs.getInt("total_kills") * 10 + rs.getInt("total_mined") - rs.getInt("total_kills") * 5);
                topTeams.add(team);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topTeams;
    }

    public void addPlayerPoints(String playerId, int points, String reason) {
        String sql = "UPDATE players SET score = score + ? WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, points);
            pstmt.setString(2, playerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlaytime(String playerId) {
        String sql = "UPDATE players SET playtime = playtime + 1 WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setTeamRole(String playerId, String teamName, TeamRole role) {
        String sql = "UPDATE player_teams SET role = ? WHERE player_id = ? AND team_id = (SELECT team_id FROM teams WHERE team_name = ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, role.name());
            pstmt.setString(2, playerId);
            pstmt.setString(3, teamName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void invitePlayer(String teamName, String playerId) {
        String sql = "INSERT INTO team_invites (team_id, player_id) VALUES ((SELECT team_id FROM teams WHERE team_name = ?), ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, teamName);
            pstmt.setString(2, playerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getTeamMembers(String teamName) {
        List<String> members = new ArrayList<>();
        String sql = """
        SELECT p.player_id
        FROM players p
        JOIN player_teams pt ON p.player_id = pt.player_id
        JOIN teams t ON pt.team_id = t.team_id
        WHERE t.team_name = ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, teamName);
            var rs = pstmt.executeQuery();
            while (rs.next()) {
                members.add(rs.getString("player_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    public int getPlayerPoints(String playerId) {
        String sql = "SELECT points FROM players WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("points");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updateScore(String playerId, ScoreType type, int amount) {
        int points = switch (type) {
            case KILL -> amount * 10;
            case DEATH -> amount * -5;
            case BLOCK_MINED -> amount;
            case ACHIEVEMENT -> amount * 15;
            case PLAYTIME -> amount;
        };

        String sql = """
            UPDATE players 
            SET points = points + ?,
                kills = CASE WHEN ? = 'KILL' THEN kills + ? ELSE kills END,
                deaths = CASE WHEN ? = 'DEATH' THEN deaths + ? ELSE deaths END,
                blocks_mined = CASE WHEN ? = 'BLOCK_MINED' THEN blocks_mined + ? ELSE blocks_mined END
            WHERE player_id = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, points);
            pstmt.setString(2, type.name());
            pstmt.setInt(3, amount);
            pstmt.setString(4, type.name());
            pstmt.setInt(5, amount);
            pstmt.setString(6, type.name());
            pstmt.setInt(7, amount);
            pstmt.setString(8, playerId);
            pstmt.executeUpdate();

            // Actualizar puntuación del equipo y notificar
            updateTeamScore(playerId, points);

            // Notificar al jugador
            Player player = Bukkit.getPlayer(UUID.fromString(playerId));
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "+" + points + " puntos (" + type + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Modificar el método updateTeamScore para que sume todos los tipos de puntos
    private void updateTeamScore(String playerId, int points) {
        String sql = """
            UPDATE teams 
            SET score = score + ? 
            WHERE team_id IN (SELECT team_id FROM player_teams WHERE player_id = ?)
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, points);
            pstmt.setString(2, playerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void leaveTeam(String playerId) {
        String sql = "DELETE FROM player_teams WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isTeamOwner(String playerId) {
        String sql = """
            SELECT t.team_id 
            FROM teams t 
            JOIN player_teams pt ON t.team_id = pt.team_id 
            WHERE pt.player_id = ? AND pt.role = 'LEADER'
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteTeam(String playerId) {
        String teamId = getPlayerTeamId(playerId);
        if (teamId != null) {
            String sql1 = "DELETE FROM player_teams WHERE team_id = ?";
            String sql2 = "DELETE FROM teams WHERE team_id = ?";
            try (PreparedStatement pstmt1 = connection.prepareStatement(sql1);
                 PreparedStatement pstmt2 = connection.prepareStatement(sql2)) {

                pstmt1.setString(1, teamId);
                pstmt1.executeUpdate();

                pstmt2.setString(1, teamId);
                pstmt2.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPlayerTeamId(String playerId) {
        String sql = "SELECT team_id FROM player_teams WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("team_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> getWinningTeam() {
        String sql = """
            SELECT t.team_name, 
                   t.score,
                   t.points,
                   COUNT(pt.player_id) as members,
                   SUM(p.kills) as total_kills,
                   SUM(p.blocks_mined) as total_mined,
                   (SUM(p.points) - SUM(p.deaths) * 5) as total_points
            FROM teams t
            JOIN player_teams pt ON t.team_id = pt.team_id
            JOIN players p ON pt.player_id = p.player_id
            GROUP BY t.team_id
            ORDER BY total_points DESC
            LIMIT 1
            """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Map<String, Object> winner = new HashMap<>();
                winner.put("name", rs.getString("team_name"));
                winner.put("score", rs.getInt("total_points"));
                winner.put("members", rs.getInt("members"));
                winner.put("kills", rs.getInt("total_kills"));
                winner.put("blocks", rs.getInt("total_mined"));
                return winner;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasTeam(String playerId) {
        String sql = "SELECT 1 FROM player_teams WHERE player_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerId);
            var rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasTeams() {
        String sql = "SELECT 1 FROM teams LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean teamExists(String teamName) {
        String sql = "SELECT 1 FROM teams WHERE team_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, teamName);
            var rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTeamMemberCount(String teamName) {
        String sql = """
            SELECT COUNT(*) as count 
            FROM player_teams pt 
            JOIN teams t ON pt.team_id = t.team_id 
            WHERE t.team_name = ?
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, teamName);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteTeamByName(String teamName) {
        String sql1 = "DELETE FROM player_teams WHERE team_id IN (SELECT team_id FROM teams WHERE team_name = ?)";
        String sql2 = "DELETE FROM teams WHERE team_name = ?";
        try (PreparedStatement pstmt1 = connection.prepareStatement(sql1);
             PreparedStatement pstmt2 = connection.prepareStatement(sql2)) {

            pstmt1.setString(1, teamName);
            pstmt1.executeUpdate();

            pstmt2.setString(1, teamName);
            pstmt2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}