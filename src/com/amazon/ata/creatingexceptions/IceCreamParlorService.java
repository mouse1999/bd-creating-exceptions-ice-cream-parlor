package com.amazon.ata.creatingexceptions;

import com.amazon.ata.creatingexceptions.dao.CartonDao;
import com.amazon.ata.creatingexceptions.exception.FlavorOutOfStockException;
import com.amazon.ata.creatingexceptions.exception.NoSuchFlavorException;
import com.amazon.ata.creatingexceptions.model.Carton;
import com.amazon.ata.creatingexceptions.model.Flavor;
import com.amazon.ata.creatingexceptions.model.Scoop;
import com.amazon.ata.creatingexceptions.model.Sundae;
import com.amazon.ata.creatingexceptions.dao.S3CartonDao;

import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Class to define behavior of an ice cream parlor.
 * Exposes operations to `IceCreamCustomer`s.
 */
public class IceCreamParlorService {

    private CartonDao cartonDao;

    public IceCreamParlorService() {
        cartonDao = new S3CartonDao();
    }

    public IceCreamParlorService(CartonDao cartonDao) {
        this.cartonDao = cartonDao;
    }

    /**
     * Retrieves a scoop of the requested flavor of ice cream.
     *
     * @param flavorName - the flavor of the ice cream scoop to retrieve.
     * @return a scoop of the desired ice cream flavor.
     */
    public Scoop getScoop(String flavorName) throws NoSuchFlavorException {
        Carton carton = cartonDao.getCarton(flavorName);

        Flavor scoopFlavor;
        try {
            scoopFlavor = carton.getFlavor();
        } catch (ResourceNotFoundException ex) {
            throw new NoSuchFlavorException(String.format("We don't serve a flavor called [%s]!", flavorName), ex);
        }
        try {
            carton.removeScoops(1);
        } catch (IllegalArgumentException ex) {
            throw new FlavorOutOfStockException("flavour out of stock");
        }



        return new Scoop(scoopFlavor);
    }

    /**
     * Retrieves an ice cream sundae containing the requested flavors.
     *
     * @param flavorNames - the list of flavors of the ice cream scoop to put
     *                    in the sundae.
     * @return a sundae containing the desired ice cream flavors.
     */
    public Sundae getSundae(List<String> flavorNames) throws NoSuchFlavorException {
        List<Flavor> flavors = Lists.newArrayList();

        for (String flavorName : flavorNames) {
            try {
                flavors.add(cartonDao.getCarton(flavorName).getFlavor());
            } catch (ResourceNotFoundException ex) {
                throw new NoSuchFlavorException(String.format("We don't serve a flavor called [%s]!", flavorName));
            }
        }

        return new Sundae(flavors);
    }
}
