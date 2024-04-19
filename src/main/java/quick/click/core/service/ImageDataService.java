package quick.click.core.service;

import org.springframework.web.multipart.MultipartFile;
import quick.click.core.domain.model.ImageData;
import quick.click.security.commons.model.AuthenticatedUser;

import java.io.IOException;
import java.util.List;

public interface ImageDataService {

    ImageData uploadImageToAdvert(Long advertId, MultipartFile file, AuthenticatedUser authenticatedUser) throws IOException;

    public ImageData findImageByIdAndByAdvertId(Long imageById, Long advertId);

    List<byte[]> findByteListToAdvert(Long advertId);

    void deleteImageByIdAndByAdvertId(Long imageById,Long advertId, AuthenticatedUser authenticatedUser);

}