import { styled } from '@mui/material/styles';
import Button from '@mui/material/Button';

export const OutlinedButton = styled(Button)({
    boxShadow: 'none',
    textTransform: 'none',
    fontSize: 18,
    fontFamily: 'Roboto',
    border: '2px solid',
    borderRadius: '20px',
    padding: '0.1em 1em 0.1em 1em',
});