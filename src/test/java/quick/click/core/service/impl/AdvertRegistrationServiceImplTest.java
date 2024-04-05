package quick.click.core.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import quick.click.core.converter.impl.AdvertCreateDtoToAdvertConverter;
import quick.click.core.converter.impl.AdvertToAdvertReadDtoConverter;
import quick.click.core.domain.dto.AdvertCreateDto;
import quick.click.core.domain.dto.AdvertReadDto;
import quick.click.core.domain.model.Advert;
import quick.click.core.repository.AdvertRepository;
import quick.click.core.repository.FileReferenceRepository;

import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static quick.click.config.factory.AdvertDtoFactory.*;
import static quick.click.config.factory.AdvertFactory.createAdvert;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdvertRegistrationServiceImpl")
class AdvertRegistrationServiceImplTest {
    @Mock
    private AdvertRepository advertRepository;
    private AdvertToAdvertReadDtoConverter typeConverterReadDto =mock(AdvertToAdvertReadDtoConverter.class);

    private AdvertCreateDtoToAdvertConverter typeConverterCreateDto = mock(AdvertCreateDtoToAdvertConverter.class);
    @Mock
    private FileReferenceRepository fileReferenceRepository;

    AdvertRegistrationServiceImpl advertRegistrationService ;
    private Advert advert;
    private AdvertReadDto advertReadDto;
    private AdvertCreateDto advertCreateDto;

    @BeforeEach
    void setUp() {
        advert = createAdvert();
        advertReadDto = createAdvertReadDto();
        advertCreateDto = createAdvertCreateDto();
        advertRegistrationService =new AdvertRegistrationServiceImpl(advertRepository,
                typeConverterReadDto, typeConverterCreateDto);

    }

    @Nested
    @DisplayName("When register an advert")
    class RegisterAdvertTests {
        @Test
        void registerAdvert_ShouldReturnAdvertReadDto() {
            when(typeConverterCreateDto.convert(advertCreateDto)).thenReturn(advert);
            when(advertRepository.saveAndFlush(advert)).thenReturn(advert);
            when(typeConverterReadDto.convert(any(Advert.class))).thenReturn(advertReadDto);

            AdvertReadDto result = advertRegistrationService.registerAdvert(advertCreateDto);

            assertEquals(advertReadDto, result);
            assertThat(result.getTitle()).isEqualTo(advertReadDto.getTitle());
            verify(advertRepository).saveAndFlush(any(Advert.class));
            assertNotNull(result);
        }
        @Test
        void testRegisterAdvert_ConversionFailure() {
            when(typeConverterCreateDto.convert(advertCreateDto)).thenThrow(new IllegalArgumentException("Invalid advert data"));

            Exception exception = assertThrows(IllegalArgumentException.class, () -> advertRegistrationService.registerAdvert(advertCreateDto));
            assertEquals("Invalid advert data", exception.getMessage());

            verify(advertRepository, never()).saveAndFlush(any(Advert.class));
            verify(typeConverterReadDto, never()).convert(any(Advert.class));
        }

        @Test
        void testRegisterAdvert_RepositorySaveFailure() {
            when(typeConverterCreateDto.convert(advertCreateDto)).thenReturn(advert);
            when(advertRepository.saveAndFlush(advert)).thenThrow(new DataAccessException("Database error") {});

            assertThrows(DataAccessException.class, () -> advertRegistrationService.registerAdvert(advertCreateDto), "Expected DataAccessException to be thrown");

            verify(advertRepository).saveAndFlush(advert);
            verify(typeConverterReadDto, never()).convert(any(Advert.class));
        }

        @Test
        void testRegisterAdvert_ShouldThrowException() {

            assertThrows(NoSuchElementException.class,
                    () -> advertRegistrationService.registerAdvert(advertCreateDto));

        }

    }

}


