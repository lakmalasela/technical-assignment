import React, { useEffect, useState } from 'react';
import { getCustomers, getCustomerById, createCustomer, updateCustomer } from '../api/customerService';
import CustomerTable from '../components/CustomerTable';
import CustomerForm from '../components/CustomerForm';
import { Container, Typography, Button } from '@mui/material';

const CustomerManager =()=>{

    const [customers, setCustomers] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(5);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);

  const [editingCustomer, setEditingCustomer] = useState(null);
  const [formVisible, setFormVisible] = useState(false);

  const loadCustomers = async (page) => {
    setLoading(true);
    try {
      const res = await getCustomers(page, size);
      const data = res.data.data; 
      setCustomers(data.content);
      setPage(data.pageNumber);
      setTotalElements(data.totalElements);
    } catch (error) {
      alert('Failed to fetch customers');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCustomers(page);
  }, [page]);

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  const handleEdit = async (id) => {
    try {
      const res = await getCustomerById(id);
      setEditingCustomer(res.data.data);
      setFormVisible(true);
    } catch {
      alert('Failed to load customer details');
    }
  };

  const handleCreate = () => {
    setEditingCustomer(null);
    setFormVisible(true);
  };

  const handleFormCancel = () => {
    setFormVisible(false);
    setEditingCustomer(null);
  };

  const handleFormSubmit = async (data) => {
    try {
      if (editingCustomer) {
        await updateCustomer(editingCustomer.id, data);
        alert('Customer updated successfully');
      } else {
        await createCustomer(data);
        alert('Customer created successfully');
      }
      setFormVisible(false);
      loadCustomers(page);
    } catch {
      alert('Failed to save customer');
    }
  };

  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>
        Customer Management
      </Typography>

      {!formVisible && (
        <>
          <Button variant="contained" sx={{ mb: 2 }} onClick={handleCreate}>
            Add Customer
          </Button>
          {loading ? <p>Loading...</p> : (
            <CustomerTable
              customers={customers}
              page={page}
              size={size}
              totalElements={totalElements}
              onPageChange={handlePageChange}
              onEdit={handleEdit}
            />
          )}
        </>
      )}

      {formVisible && (
        <CustomerForm
          initialValues={editingCustomer}
          onSubmit={handleFormSubmit}
          onCancel={handleFormCancel}
        />
      )}
    </Container>
  );

}
export default CustomerManager