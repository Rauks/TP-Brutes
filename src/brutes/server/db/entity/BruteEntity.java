package brutes.server.db.entity;

import brutes.server.db.DatasManager;
import brutes.server.db.Entity;
import brutes.server.game.Brute;
import brutes.server.game.User;
import brutes.server.ui;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Thiktak
 */
public class BruteEntity implements Entity {

    public static Brute create(ResultSet r) throws IOException, SQLException {
        Brute brute = new Brute(r.getInt("id"), r.getString("name"), r.getShort("level"), r.getShort("life"), r.getShort("strength"), r.getShort("speed"), r.getInt("id") /* TODO: change ID -> IMG */);
        brute.setUserId(r.getInt("user_id"));
        brute.setImageID(r.getInt("image_id"));
        brute.setBonuses(BonusEntity.findAllByBrute(brute));
        return brute;
    }

    public static int save(Connection con, Brute brute) throws IOException, SQLException {
        PreparedStatement psql = con.prepareStatement("UPDATE Brutes SET name = ?, level = ?, life = ?, strength = ?, speed = ?, image_id = ? WHERE id = ?");
        psql.setString(1, brute.getName());
        psql.setInt(2, brute.getLevel());
        psql.setInt(3, brute.getLife());
        psql.setInt(4, brute.getStrength());
        psql.setInt(5, brute.getSpeed());
        psql.setInt(6, brute.getImageID());
        psql.setInt(7, brute.getId());
        return psql.executeUpdate();
    }

    public static Brute insert(Connection con, Brute brute) throws IOException, SQLException {
        PreparedStatement psql = con.prepareStatement("INSERT INTO Brutes (user_id, name, level, life, strength, speed, image_id) VALUES(?, ?, ?, ?, ?, ?, ?)");
        psql.setInt(1, brute.getUserId());
        psql.setString(2, brute.getName());
        psql.setInt(3, brute.getLevel());
        psql.setInt(4, brute.getLife());
        psql.setInt(5, brute.getStrength());
        psql.setInt(6, brute.getImageID());
        psql.setInt(7, brute.getSpeed());
        return findById(psql.executeUpdate());
    }

    public static int delete(Connection con, Brute brute) throws IOException, SQLException {
        PreparedStatement psql = con.prepareStatement("DELETE FROM Brutes WHERE id = ?");
        psql.setInt(1, brute.getId());
        return psql.executeUpdate();
    }

    public static Brute findById(int id) throws IOException, SQLException {
        PreparedStatement psql = DatasManager.prepare("SELECT * FROM Brutes WHERE id = ?");
        psql.setInt(1, id);
        ResultSet rs = psql.executeQuery();
        if (rs.next()) {
            return BruteEntity.create(rs);
        }
        return null;
    }

    public static Brute findOneById(int id) throws IOException, SQLException, NotFoundEntityException {
        Brute object = findById(id);
        if (object == null) {
            throw new NotFoundEntityException(Brute.class);
        }
        return object;
    }

    public static Brute findByUser(User user) throws IOException, SQLException {
        PreparedStatement psql = DatasManager.prepare("SELECT * FROM brutes WHERE user_id = ? ORDER BY id DESC");
        psql.setInt(1, user.getId());
        ResultSet rs = psql.executeQuery();
        if (rs.next()) {
            return BruteEntity.create(rs);
        }
        return null;
    }

    public static Brute findOneByUser(User user) throws IOException, SQLException, NotFoundEntityException {
        Brute object = findByUser(user);
        if (object == null) {
            throw new NotFoundEntityException(User.class);
        }
        return object;
    }

    public static Brute findRandomAnotherToBattleByUser(User user, int level, double i) throws IOException, SQLException, NotFoundEntityException {
        double level_min = 0; //level/(i+1);//(int) (level - 5 - Math.sqrt(level_i))/i;
        double level_max = 200;//level*(i+1);//level + 5 + level_i;
        
        PreparedStatement psql = DatasManager.prepare("SELECT * FROM Brutes WHERE user_id <> ? AND level BETWEEN (" + level_min + ") AND (" + level_max + ") ORDER BY RANDOM() LIMIT 1");
        psql.setInt(1, user.getId());
        ResultSet rs = psql.executeQuery();

        if (rs.next()) {
            return BruteEntity.create(rs);
        }
        return level > 100 ? null : findRandomAnotherToBattleByUser(user, level, ++i);
    }

    public static Brute findOneRandomAnotherToBattleByUser(User user) throws IOException, SQLException, NotFoundEntityException {
        Brute object = findRandomAnotherToBattleByUser(user, BruteEntity.findOneByUser(user).getLevel(), 1);
        if (object == null) {
            throw new NotFoundEntityException(User.class);
        }
        return object;
    }

    public static Brute findByName(String name) throws IOException, SQLException {
        PreparedStatement psql = DatasManager.prepare("SELECT * FROM Brutes WHERE name = ? ORDER BY id DESC");
        psql.setString(1, name);
        ResultSet rs = psql.executeQuery();

        if (rs.next()) {
            return BruteEntity.create(rs);
        }
        return null;
    }
}
