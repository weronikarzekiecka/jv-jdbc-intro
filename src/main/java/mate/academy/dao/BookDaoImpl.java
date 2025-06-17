package mate.academy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.academy.exception.DataProcessingException;
import mate.academy.model.Book;
import mate.academy.util.ConnectionUtil;

@mate.academy.dao.Dao
public class BookDaoImpl implements BookDao {
    @Override
    public Book create(Book book) {
        String query = "INSERT INTO books (title, price) VALUES (?, ?)";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, book.getTitle());
            statement.setBigDecimal(2, book.getPrice());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                book.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Failed to create a book", e);
        }
        return book;
    }

    @Override
    public Optional<Book> findById(Long id) {
        String query = "SELECT * FROM books WHERE id = ?";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Book book = new Book(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getBigDecimal("price")
                );
                return Optional.of(book);
            }

        } catch (SQLException e) {
            throw new DataProcessingException("Failed to find a book by id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";
        try (Connection connection = ConnectionUtil.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getLong("id"),
                        resultSet.getString("title"),
                        resultSet.getBigDecimal("price")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Failed to locate all books", e);
        }
        return books;
    }

    @Override
    public Book update(Book book) {
        String query = "UPDATE books SET title = ?, price = ? WHERE id = ?";
            try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, book.getTitle());
            statement.setBigDecimal(2, book.getPrice());
            statement.setLong(3, book.getId());
            statement.executeUpdate();
            return book;
        } catch (SQLException e) {
            throw new DataProcessingException("Failed to update the boo by id: " + book.getId(), e);
        }
    }

    @Override
    public boolean removeById(Long id) {
        String query = "DELETE FROM books WHERE id = ?";
            try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            int rowsChanged = statement.executeUpdate();
            return rowsChanged > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Failed to remove a book by id: " + id, e);
        }
    }
}
