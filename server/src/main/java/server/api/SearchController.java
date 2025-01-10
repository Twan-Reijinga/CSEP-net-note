package server.api;

import commons.NoteTitle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.service.SearchService;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /** This method handles the GET request to the address bellow and passes the data
     * to the service that performs the search.
     *
     * @param keywords the words by which the search is performed
     * @param matchAll indicator whether the search should match all keywords or not
     * @param searchIn a setting to specify whether to search only in the title, only in the content or both of them.
     * @return list of all notes that match the search options
     */
    @GetMapping(path = "/{keywords}/{matchAll}/{searchIn}")
    public ResponseEntity<List<NoteTitle>> searchNotes(@PathVariable String keywords,
                                                       @PathVariable String matchAll, @PathVariable String searchIn) {

        // TODO: null for collection id (instead of UUID)
        List<NoteTitle> result = searchService.getSearchResults(null, keywords,
                                                                Boolean.valueOf(matchAll), searchIn);

        return ResponseEntity.ok(result);
    }



}
