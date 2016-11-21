package com.example.Naresh.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "pointsApi",
        version = "v1",
        resource = "points",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.Naresh.example.com",
                ownerName = "backend.myapplication.Naresh.example.com",
                packagePath = ""
        )
)
public class PointsEndpoint {

    private static final Logger logger = Logger.getLogger(PointsEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Points.class);


    }


    /**
     * Returns the {@link Points} with the corresponding ID.
     *
     * @param userId the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Points} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "points/{userId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Points get(@Named("userId") Long userId) throws NotFoundException {
        logger.info("Getting Points with ID: " + userId);
        Points points = ofy().load().type(Points.class).id(userId).now();
        if (points == null) {
            throw new NotFoundException("Could not find Points with ID: " + userId);
        }
        return points;
    }

    /**
     * Inserts a new {@code Points}.
     */
    @ApiMethod(
            name = "insert",
            path = "points",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Points insert(Points points) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that points.userId has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(points).now();
        logger.info("Created Points with ID: " + points.getUserId());

        return ofy().load().entity(points).now();
    }

    /**
     * Updates an existing {@code Points}.
     *
     * @param userId the ID of the entity to be updated
     * @param points the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code userId} does not correspond to an existing
     *                           {@code Points}
     */
    @ApiMethod(
            name = "update",
            path = "points/{userId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Points update(@Named("userId") Long userId, Points points) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(userId);
        ofy().save().entity(points).now();
        logger.info("Updated Points: " + points);
        return ofy().load().entity(points).now();
    }

    /**
     * Deletes the specified {@code Points}.
     *
     * @param userId the ID of the entity to delete
     * @throws NotFoundException if the {@code userId} does not correspond to an existing
     *                           {@code Points}
     */
    @ApiMethod(
            name = "remove",
            path = "points/{userId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("userId") Long userId) throws NotFoundException {
        checkExists(userId);
        ofy().delete().type(Points.class).id(userId).now();
        logger.info("Deleted Points with ID: " + userId);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "points",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Points> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Points> query = ofy().load().type(Points.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Points> queryIterator = query.iterator();
        List<Points> pointsList = new ArrayList<Points>(limit);
        while (queryIterator.hasNext()) {
            pointsList.add(queryIterator.next());
        }
        return CollectionResponse.<Points>builder().setItems(pointsList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long userId) throws NotFoundException {
        try {
            ofy().load().type(Points.class).id(userId).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Points with ID: " + userId);
        }
    }
}
