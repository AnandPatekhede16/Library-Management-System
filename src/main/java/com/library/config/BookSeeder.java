package com.library.config;

import com.library.entity.Book;
import com.library.entity.Category;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Order(2) // Run after DataInitializer (which sets up categories)
@RequiredArgsConstructor
@Slf4j
public class BookSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (bookRepository.count() < 5) {
            log.info("Database has few books. Commencing massive book injection...");
            seedKnowledgeBase();
        } else {
            log.info("Database already contains books. Skipping massive injection.");
        }
    }

    private void seedKnowledgeBase() {
        Optional<Category> techOpt = categoryRepository.findByName("Computer");
        Optional<Category> sciOpt = categoryRepository.findByName("Science");
        Optional<Category> histOpt = categoryRepository.findByName("History");
        Optional<Category> ficOpt = categoryRepository.findByName("Novels");
        Optional<Category> selfOpt = categoryRepository.findByName("Self-Help");

        if (techOpt.isEmpty() || ficOpt.isEmpty() || sciOpt.isEmpty() || histOpt.isEmpty() || selfOpt.isEmpty()) {
            log.warn("Categories not found, skipping book seeding.");
            return;
        }

        Category tech = techOpt.get();
        Category sci = sciOpt.get();
        Category hist = histOpt.get();
        Category fic = ficOpt.get();
        Category self = selfOpt.get();

        // --- Technology Books ---
        addBook("Clean Code", "Robert C. Martin", "978-0132350884", 2008, 10, tech, "Even bad code can function. But if code isn't clean, it can bring a development organization to its knees.");
        addBook("Design Patterns", "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", "978-0201633610", 1994, 5, tech, "Capturing a wealth of experience about the design of object-oriented software.");
        addBook("The Pragmatic Programmer", "Andrew Hunt, David Thomas", "978-0201616224", 1999, 8, tech, "A classic book on software engineering and best practices.");
        addBook("Introduction to Algorithms", "Thomas H. Cormen", "978-0262033848", 2009, 12, tech, "A comprehensive textbook covering the modern study of computer algorithms.");
        addBook("Grokking Algorithms", "Aditya Bhargava", "978-1617292231", 2016, 7, tech, "An illustrated guide for programmers and other curious people.");
        addBook("Artificial Intelligence: A Modern Approach", "Stuart Russell, Peter Norvig", "978-0136042594", 2009, 4, tech, "The standard AI text worldwide.");
        
        // --- Science Books ---
        addBook("A Brief History of Time", "Stephen Hawking", "978-0553380163", 1988, 15, sci, "A landmark volume in science writing by one of the great minds of our time.");
        addBook("Cosmos", "Carl Sagan", "978-0345331359", 1980, 10, sci, "Cosmos explores 15 billion years of cosmic evolution and the development of science and civilization.");
        addBook("The Selfish Gene", "Richard Dawkins", "978-0192860927", 1976, 6, sci, "A classic exposition of evolutionary thought.");
        addBook("Sapiens: A Brief History of Humankind", "Yuval Noah Harari", "978-0062316097", 2011, 20, sci, "A groundbreaking narrative of humanity's creation and evolution.");
        addBook("Astrophysics for People in a Hurry", "Neil deGrasse Tyson", "978-0393609394", 2017, 12, sci, "What is the nature of space and time? How do we fit within the universe?");

        // --- History Books ---
        addBook("Guns, Germs, and Steel", "Jared Diamond", "978-0393317558", 1997, 8, hist, "The fates of human societies.");
        addBook("The Diary of a Young Girl", "Anne Frank", "978-0553296983", 1947, 25, hist, "Discovered in the attic in which she spent the last years of her life.");
        addBook("1776", "David McCullough", "978-0743226721", 2005, 7, hist, "The story of those who marched with General George Washington in the year of the Declaration of Independence.");
        addBook("Team of Rivals", "Doris Kearns Goodwin", "978-0743270755", 2005, 5, hist, "The political genius of Abraham Lincoln.");

        // --- Fiction Books ---
        addBook("1984", "George Orwell", "978-0451524935", 1949, 30, fic, "Among the seminal texts of the 20th century, Nineteen Eighty-Four is a rare work that grows more haunting as its futuristic purgatory becomes more real.");
        addBook("To Kill a Mockingbird", "Harper Lee", "978-0060935467", 1960, 20, fic, "A gripping, heart-wrenching, and wholly remarkable tale of coming-of-age in a South poisoned by virulent prejudice.");
        addBook("The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565", 1925, 15, fic, "The story of the mysteriously wealthy Jay Gatsby and his love for the beautiful Daisy Buchanan.");
        addBook("Pride and Prejudice", "Jane Austen", "978-0141439518", 1813, 10, fic, "An classic of English literature, written with incisive wit and superb character delineation.");
        addBook("Dune", "Frank Herbert", "978-0441172719", 1965, 22, fic, "Set on the desert planet Arrakis, Dune is the story of the boy Paul Atreides.");
        addBook("The Hobbit", "J.R.R. Tolkien", "978-0547928227", 1937, 25, fic, "A great modern classic and the prelude to The Lord of the Rings.");
        addBook("Fahrenheit 451", "Ray Bradbury", "978-1451673319", 1953, 18, fic, "A dystopian novel about a future American society where books are outlawed and firemen burn any that are found.");

        // --- Self-Help Books ---
        addBook("Atomic Habits", "James Clear", "978-0735211292", 2018, 40, self, "No matter your goals, Atomic Habits offers a proven framework for improving--every day.");
        addBook("The 7 Habits of Highly Effective People", "Stephen R. Covey", "978-1982137274", 1989, 15, self, "A step-by-step pathway for living with fairness, integrity, honesty, and human dignity.");
        addBook("Thinking, Fast and Slow", "Daniel Kahneman", "978-0374533557", 2011, 10, self, "The groundbreaking tour of the mind and explains the two systems that drive the way we think.");
        addBook("How to Win Friends and Influence People", "Dale Carnegie", "978-0671027032", 1936, 20, self, "You can go after the job you want—and get it! You can take the job you have—and improve it!");
        addBook("Deep Work", "Cal Newport", "978-1455586691", 2016, 12, self, "Rules for focused success in a distracted world.");

        log.info("Massive book injection complete. Added ~25 new titles.");
    }

    private void addBook(String title, String author, String isbn, int year, int copies, Category cat, String desc) {
        if (!bookRepository.existsByIsbn(isbn)) {
            Book book = Book.builder()
                .title(title)
                .author(author)
                .isbn(isbn)
                .publishedYear(year)
                .totalCopies(copies)
                .availableCopies(copies)
                .category(cat)
                .description(desc)
                .build();
            bookRepository.save(book);
        }
    }
}
