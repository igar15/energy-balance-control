package ru.javaprojects.bxservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.bxservice.model.BasicExchange;
import ru.javaprojects.bxservice.repository.BasicExchangeRepository;
import ru.javaprojects.bxservice.to.UserBxDetails;
import ru.javaprojects.bxservice.service.client.UserServiceClient;

import java.time.LocalDate;
import java.util.List;

import static ru.javaprojects.bxservice.to.UserBxDetails.Sex.MAN;
import static ru.javaprojects.bxservice.util.BasicExchangeUtil.calculateBxCalories;

@Service
public class BasicExchangeService {
    private final BasicExchangeRepository repository;
    //TODO Autowired real FeignClient
    private UserServiceClient userServiceClient = (userId, token) -> new UserBxDetails(MAN, 90, 185, 34);

    public BasicExchangeService(BasicExchangeRepository repository) {
        this.repository = repository;
    }

    public Integer getBxCalories(LocalDate date, long userId) {
        Assert.notNull(date, "date must not be null");
        return repository.findByUserIdAndDate(userId, date)
                .orElseGet(() -> create(date, userId))
                .getCalories();
    }

    public BasicExchange create(LocalDate date, long userId) {
        Assert.notNull(date, "date must not be null");
        int bxCalories = getBxCalories(userId);
        BasicExchange basicExchange = new BasicExchange(null, date, bxCalories, userId);
        return repository.save(basicExchange);
    }

    @Transactional
    public void updateBasicExchanges(LocalDate date, long userId) {
        Assert.notNull(date, "date must not be null");
        List<BasicExchange> basicExchanges = repository.findAllByUserIdAndDateGreaterThanEqual(userId, date);
        if (!basicExchanges.isEmpty()) {
            int bxCalories = getBxCalories(userId);
            basicExchanges.forEach(basicExchange -> basicExchange.setCalories(bxCalories));
        }
    }

    private int getBxCalories(long userId) {
        UserBxDetails userBxDetails = userServiceClient.getUserBxDetails(userId);
        return calculateBxCalories(userBxDetails);
    }

    //use only for tests
    void setUserServiceClient(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }
}