package DAO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DAOInterface <T>{
    public boolean add(T t) throws SQLException;
    public boolean update(T t) throws SQLException;
    public int xoa(T t);
    public ArrayList<T> selectAll();
    public T selectByName(T t) throws SQLException;
    public ArrayList<T> selectByCondition(String condition);
}
