package com.sst.nt.lms.orch.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.sst.nt.lms.orch.model.Borrower;
import com.sst.nt.lms.orch.model.Branch;
import com.sst.nt.lms.orch.model.Loan;
import com.sst.nt.lms.orch.util.MapBuilder;

/**
 * Controller for administrators with the power to manage branch and borrower
 * details and override due dates.
 *
 * <p>FIXME: Limit access to most of these endpoints to authorized users
 * @author Jonathan Lovelace
 */
@RestController
public final class ExecutiveController {
	/**
	 * REST delegate helper.
	 */
	@Autowired
	private RestTemplate delegate;

	/**
	 * Helper method to reduce the amount of repetitive code required for "get-all"
	 * methods.
	 * @param url the URL to send the REST request to
	 * @param <T> the type we expect
	 * @return the response the server sent
	 */
	private <T> ResponseEntity<T> getAll(final String url) {
		return delegate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<T>() {});
	}
	/**
	 * Get all branches from the administrator service.
	 * @return the list of all branches, or other response
	 */
	// @GetMapping({"/branches", "/branches/"}) // Conflicts with other controller's route.
	public ResponseEntity<List<Branch>> getBranches() {
		return this.<List<Branch>>getAll("http://admin/branches");
	}
	/**
	 * Get all borrowers from the administrator service.
	 * @return the list of all borrowers, or other response
	 */
	@GetMapping({"/borrowers", "/borrowers/"})
	public ResponseEntity<List<Borrower>> getBorrowers() {
		return this.<List<Borrower>>getAll("http://admin/borrowers");
	}
	/**
	 * Get a branch by its ID number from the administrator service.
	 * @param branchId the ID number of the branch
	 * @return the branch with that ID, or other response
	 */
	// @GetMapping({"/branch/{branchId}", "/branch/{branchId}/"}) // conflicts with other controller's routes
	public ResponseEntity<Branch> getBranch(
			@PathVariable("branchId") final int branchId) {
		return delegate.getForEntity("http://admin/branch/" + branchId,
				Branch.class);
	}

	/**
	 * Get a borrower by his or her card number from the administrator service.
	 * @param cardNumber the borrower's card number
	 * @return the borrower with that card number, or other response
	 */
	@GetMapping({"/borrower/{cardNumber}", "/borrower/{cardNumber}/"})
	public ResponseEntity<Borrower> getBorrower(
			@PathVariable("cardNumber") final int cardNumber) {
		return delegate.getForEntity("http://admin/borrower/" + cardNumber,
				Borrower.class);
	}
	/**
	 * Update a branch by its ID number in the administrator service.
	 * @param branchId the ID number of the branch to update
	 * @param input the branch data to update
	 * @return the updated branch, or other response
	 */
	// @PutMapping({ "/branch/{branchId}", "/branch/{branchId}/" }) // conflicts with other controller's routes
	public ResponseEntity<Branch> updateBranch(@PathVariable("branchId") final int branchId,
			@RequestBody final Branch input) {
		return delegate.exchange("http://admin/branch/" + branchId, HttpMethod.PUT,
				new HttpEntity<>(input), Branch.class);
	}
	/**
	 * Update a borrower by his or her card number in the administrator service.
	 * @param cardNumber the card number of the borrower to update
	 * @param input the borrower details to update
	 * @return the updated borrower, or other response
	 * @throws TransactionException if borrower not found or on internal error
	 */
	@PutMapping({ "/borrower/{cardNumber}", "/borrower/{cardNumber}/" })
	public ResponseEntity<Borrower> updateBorrower(
			@PathVariable("cardNumber") final int cardNumber,
			@RequestBody final Borrower input) {
		return delegate.exchange("http://admin/borrower/" + cardNumber,
				HttpMethod.PUT, new HttpEntity<>(input), Borrower.class);
	}
	/**
	 * Create a branch with the given name and address in the administrator service.
	 * @param body the request body, which must contain a 'name' field; an
	 *             'address' field is also recognized.
	 * @return the created branch, or other response
	 */
	@PostMapping({"/branch", "/branch/"})
	public ResponseEntity<Branch> createBranch(@RequestBody final Map<String, String> body) {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return delegate.postForEntity("http://admin/branch", new HttpEntity(body, headers),
				Branch.class);
	}

	/**
	 * Create a borrower record with the given name, address, and phone data in the
	 * administrator service.
	 *
	 * @param body the request body, which must contain a 'name' field;
	 *             'address' and 'phone' fields are also recognized.
	 * @return the created borrower record, or other response
	 */
	@PostMapping({"/borrower", "/borrower/"})
	public ResponseEntity<Borrower> createBorrower(@RequestBody final Map<String, String> body) {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return delegate.postForEntity("http://admin/borrower", new HttpEntity(body, headers),
				Borrower.class);
	}
	/**
	 * Delete the branch with the given ID in the administrator service.
	 * @param branchId the ID of the branch to delete.
	 */
	@DeleteMapping({"/branch/{branchId}", "/branch/{branchId}/"})
	public void deleteBranch(@PathVariable("branchId") final int branchId) {
		delegate.delete("http://admin/branch/" + branchId);
	}
	/**
	 * Delete the borrower with the given card number in the administrator service.
	 * @param cardNumber the card number of the borrower to delete
	 */
	@DeleteMapping({ "/borrower/{cardNumber}", "/borrower/{cardNumber}/" })
	public void deleteBorrower(@PathVariable("cardNumber") final int cardNumber) {
		delegate.delete("http://admin/borrower/" + cardNumber);
	}

	/**
	 * Override the due date of a loan in the administrator service.
	 *
	 * @param borrowerId the card number of the borrower who checked out the book in
	 *                   question
	 * @param branchId   the ID number of the branch from which the book was checked
	 *                   out
	 * @param bookId     the ID number of the book in question
	 * @param dueDate    the new due date
	 * @return the updated loan record, or other response
	 */
	@PutMapping("/loan/book/{bookId}/branch/{branchId}/borrower/{borrowerId}/due")
	public ResponseEntity<Loan> overrideDueDate(
			@PathVariable("bookId") final int bookId,
			@PathVariable("branchId") final int branchId,
			@PathVariable("borrowerId") final int borrowerId,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) final LocalDate dueDate) {
		return delegate.exchange(
				"http://admin/loan/book/" + bookId + "/branch/" + branchId
						+ "/borrower/" + borrowerId + "/due",
				HttpMethod.PUT, new HttpEntity<>(dueDate), Loan.class);
	}
	/**
	 * Get the date a book is due back to the branch from which it was borrowed.
	 * @param borrowerId the card number of the borrower who checked out the book in question
	 * @param branchId the ID number of the branch from which the book was checked out
	 * @param bookId the ID number of the book in question
	 * @return the updated loan record
	 * @throws TransactionException if no such borrower, branch, book, or loan, or on internal error
	 */
	@GetMapping("/loan/book/{bookId}/branch/{branchId}/borrower/{borrowerId}/due")
	public ResponseEntity<LocalDate> getDueDate(
			@PathVariable("bookId") final int bookId,
			@PathVariable("branchId") final int branchId,
			@PathVariable("borrowerId") final int borrowerId) {
		return delegate.getForEntity("http://admin/loan/book/" + bookId + "/branch/"
				+ branchId + "/borrower/" + borrowerId + "/due", LocalDate.class);
	}
}
