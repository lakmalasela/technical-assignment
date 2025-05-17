import React from 'react';
import { Table, TableBody, TableCell, TableHead, TableRow, TablePagination, Button } from '@mui/material';

const CustomerTable = ({ customers, page, size, totalElements, onPageChange, onEdit })=>{

  return (
    <>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>ID</TableCell>
            <TableCell>Name</TableCell>
            <TableCell>NIC</TableCell>
            <TableCell>Date of Birth</TableCell>
            <TableCell>Mobile Numbers</TableCell>
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {customers.map(cust => (
            <TableRow key={cust.id}>
              <TableCell>{cust.id}</TableCell>
              <TableCell>{cust.name}</TableCell>
              <TableCell>{cust.nic}</TableCell>
              <TableCell>{cust.dateOfBirth}</TableCell>
              <TableCell>{cust.mobileNumbers?.join(', ')}</TableCell>
              <TableCell>
                <Button variant="outlined" size="small" onClick={() => onEdit(cust.id)}>
                  Edit
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <TablePagination
        component="div"
        count={totalElements}
        page={page}
        onPageChange={(_, newPage) => onPageChange(newPage)}
        rowsPerPage={size}
        rowsPerPageOptions={[size]}
        labelDisplayedRows={({ from, to, count }) => {
        const actualFrom = page * size + 1;
        const actualTo = Math.min((page + 1) * size, count);
        return `${actualFrom}–${actualTo} of ${count}`;
        }}
      />
    </>
  );

}
export default CustomerTable