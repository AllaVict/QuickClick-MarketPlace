package quick.click.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quick.click.commons.exeptions.AuthorizationException;
import quick.click.commons.exeptions.ResourceNotFoundException;
import quick.click.core.converter.TypeConverter;
import quick.click.core.domain.dto.AdvertReadDto;
import quick.click.core.domain.dto.AdvertReadWithoutAuthDto;
import quick.click.core.domain.model.Advert;
import quick.click.core.domain.model.User;
import quick.click.core.enums.Category;
import quick.click.core.repository.AdvertRepository;
import quick.click.core.repository.UserRepository;
import quick.click.core.service.AdvertSearchService;
import quick.click.security.commons.model.AuthenticatedUser;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for handling search operations related to adverts.
 *
 * @author Alla Borodina
 */
@Service
@RequiredArgsConstructor
public class AdvertSearchServiceImpl implements AdvertSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertSearchServiceImpl.class);

    @Autowired
    private final AdvertRepository advertRepository;

    private final UserRepository userRepository;

    private final TypeConverter<Advert, AdvertReadDto> typeConverterReadDto;

    private final TypeConverter<Advert, AdvertReadWithoutAuthDto> typeConverterReadWithoutAuthDto;


    /**
     * Finds an advert by its ID.
     *
     * @param advertId The ID of the advert to find.
     * @return An AdvertReadWithoutAuthDto containing the advert details if found.
     * @throws ResourceNotFoundException If no advert is found with the given ID.
     */
    @Override
    public AdvertReadWithoutAuthDto findAdvertById(final Long advertId) {

        final AdvertReadWithoutAuthDto advertReadWithoutAuthDto = advertRepository.findById(advertId)
                .map(typeConverterReadWithoutAuthDto::convert)
                .orElseThrow(() -> new ResourceNotFoundException("Advert", "id", advertId));

        LOGGER.debug("In findAdvertById find the Advert with id: {}", advertReadWithoutAuthDto.getId());

        return advertReadWithoutAuthDto;
    }

    /**
     * Retrieves all adverts.
     *
     * @return A list of AdvertReadDto containing details of all adverts.
     */
    @Override
    public List<AdvertReadWithoutAuthDto> findAllAdverts() {

        final List<AdvertReadWithoutAuthDto> advertReadWithoutAuthDtoList = advertRepository.findAll()
                .stream()
                .map(typeConverterReadWithoutAuthDto::convert)
                .toList();

        LOGGER.debug("In findAllAdverts find all adverts");

        return advertReadWithoutAuthDtoList;
    }

    /**
     * Retrieves all adverts ordered by creation date in descending order.
     *
     * @return A list of AdvertReadWithoutAuthDto containing details of all adverts sorted by creation date.
     */
    @Override
    public List<AdvertReadDto> findAllByOrderByCreatedDateDesc() {

        final List<AdvertReadDto> advertReadDtoList = advertRepository.findAllByOrderByCreatedDateDesc()
                .stream()
                .map(typeConverterReadDto::convert)
                .toList();

        LOGGER.debug("In findAllByOrderByCreatedDateDesc find all adverts sorted by createdDate desc");

        return advertReadDtoList;
    }

    /**
     * Finds all adverts created by a specific user.
     *
     * @param authenticatedUser The authenticated user whose adverts are to be found.
     * @return A list of AdvertReadDto containing details of the adverts created by the specified user.
     * @throws AuthorizationException If the authenticated user cannot be authorized.
     */
    @Override
    public List<AdvertReadDto> findAllAdvertsByUser(final AuthenticatedUser authenticatedUser) {

        final User user = getUserByAuthenticatedUser(authenticatedUser);

        final List<AdvertReadDto> advertReadDtoList = advertRepository.findAllByUserOrderByCreatedDateDesc(user)
                .stream()
                .map(typeConverterReadDto::convert)
                .toList();

        LOGGER.debug("In findAllAdvertsByUser find all adverts for the user with id: {}, {}", user.getId(), advertReadDtoList);

        return advertReadDtoList;
    }

    /**
     * Retrieves all adverts with certain category.
     *
     * @param category The category by which all ads related to it are to be found.
     * @return A list of AdvertReadDto containing details of all adverts with certain category.
     * @throws IllegalArgumentException If the input category is out of the related enum range.
     */
    @Override
    public List<AdvertReadWithoutAuthDto> findByCategory(final String category) throws IllegalArgumentException {
        Category categoryToSearch = findCategoryByString(category);
        final List<AdvertReadWithoutAuthDto> advertReadWithoutAuthDtoList = advertRepository.findByCategory(categoryToSearch)
                .stream()
                .map(typeConverterReadWithoutAuthDto::convert)
                .toList();
        LOGGER.debug("In findByCategory find all adverts with category {}", category);

        return advertReadWithoutAuthDtoList;
    }

    /**
     * Retrieves all adverts with discounted price.
     *
     * @return A list of AdvertReadDto containing details of all adverts with discounted price.
     */
    @Override
    public List<AdvertReadWithoutAuthDto> findDiscounted() {
        final List<AdvertReadWithoutAuthDto> advertReadWithoutAuthDtoList = advertRepository.findDiscounted()
                .stream()
                .map(typeConverterReadWithoutAuthDto::convert)
                .toList();
        LOGGER.debug("In findDiscounted find all adverts with discounted price");

        return advertReadWithoutAuthDtoList;
    }

    /**
     * Retrieves 10 adverts with max viewing quantity.
     *
     * @return A list of AdvertReadDto containing details of 10 adverts with max viewing quantity.
     */
    @Override
    public List<AdvertReadWithoutAuthDto> find10MaxViewed() {
        final List<AdvertReadWithoutAuthDto> advertReadWithoutAuthDtoList = advertRepository.find10MaxViewed()
                .stream()
                .map(typeConverterReadWithoutAuthDto::convert)
                .toList();
        LOGGER.debug("In find10MaxViewed find 10 adverts with max viewing quantity");

        return advertReadWithoutAuthDtoList;
    }

    /**
     * Retrieves all adverts which are promoted (i.e. participated in some promotion).
     *
     * @return A list of AdvertReadDto containing details of all adverts which are promoted.
     */
    @Override
    public List<AdvertReadWithoutAuthDto> findPromoted() {
        final List<AdvertReadWithoutAuthDto> advertReadWithoutAuthDtoList = advertRepository.findPromoted()
                .stream()
                .map(typeConverterReadWithoutAuthDto::convert)
                .toList();
        LOGGER.debug("In findPromoted find all adverts which are promoted");

        return advertReadWithoutAuthDtoList;
    }

    @Override
    public Set<AdvertReadDto> findViewed(User user) {
        return user.getViewedAdverts()
                .stream()
                .map(typeConverterReadDto::convert)
                .collect(Collectors.toSet());
    }

    private User getUserByAuthenticatedUser(final AuthenticatedUser authenticatedUser) {
        String username = authenticatedUser.getEmail();
        return userRepository.findUserByEmail(username)
                .orElseThrow(() -> new AuthorizationException("Unauthorized access"));

    }

    private Category findCategoryByString(String category) {
        for (Category value : Category.values()) {
            if (value.name().equals(category.toUpperCase())) {
                return value;
            }
        }
        throw new IllegalArgumentException("There is no such category: " + category);
    }

}
