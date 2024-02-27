package quick.click.advertservice.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quick.click.advertservice.core.domain.dto.AdvertReadDto;
import quick.click.advertservice.core.service.AdvertSearchService;

import java.util.List;

import static quick.click.advertservice.commons.config.ApiVersion.VERSION_1_0;
import static quick.click.advertservice.commons.constants.Constants.Endpoints.ADVERTS_URL;
import static quick.click.advertservice.core.controller.AdvertSearchController.BASE_URL;

@RestController
@RequestMapping(BASE_URL)
public class AdvertSearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertSearchController.class);

    public static final String BASE_URL = VERSION_1_0 + ADVERTS_URL;

    public  final AdvertSearchService advertSearchService;

    public AdvertSearchController(final AdvertSearchService advertSearchService) {
        this.advertSearchService = advertSearchService;
    }

    /**
     * GET   http://localhost:8081/v1.0/adverts/1
     @GetMapping("/adverts/{id}")
     public AdvertReadDto findById(@PathVariable("id") Long id) {
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdvertReadDto> findAdvertById(@PathVariable("id") Long advertId) {

        LOGGER.debug("In findAdvertById received GET find the advert successfully with id {} "+advertId);

        //  final AdvertReadDto advertReadDto = Optional.ofNullable(advertSearchService.findAdvertById(advertId))
      //          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        final AdvertReadDto advertReadDto = advertSearchService.findAdvertById(advertId);
        LOGGER.debug("In findAdvertById received GET find the advert successfully with id {} ", advertReadDto.getId());

        return ResponseEntity.status(HttpStatus.OK).body(advertReadDto);

    }

    /**
     GET    http://localhost:8081/v1.0/adverts
     @GetMapping("/adverts")
     public ResponseEntity<List<AdvertReadDto>> findAll(){
     */
    @GetMapping()
    public ResponseEntity<List<AdvertReadDto>> findAllAdverts() {

        LOGGER.debug("In findAllAdvert received GET find all advert successfully ");

       // final List<AdvertReadDto> advertReadDtoList = Optional.ofNullable(advertSearchService.findAllAdverts())
       //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        final List<AdvertReadDto> advertReadDtoList = advertSearchService.findAllAdverts();

        LOGGER.debug("In findAllAdvert received GET find all advert successfully ");

        return ResponseEntity.status(HttpStatus.OK).body(advertReadDtoList);

    }

}



