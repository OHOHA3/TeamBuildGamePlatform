import { styled } from '@mui/material/styles';
import Button from '@mui/material/Button';
import { Paper } from '@mui/material';

const QButton = styled(Button)({
  width: "20%",
  height: "95%",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  borderRadius: "16px",
  position: 'absolute', 
  zIndex: '3',
  background: "linear-gradient(135deg, #0b65ff, #7ce1fe)",
  '&:hover': {
    background: "#7ce1fe",
    boxShadow: 'none',
  },
});

function QuestionButton(props) {
    return (
      <QButton 
        component={Paper} 
        elevation={3} 
        disabled={props.disabled}
        onClick={props.onClick}
      >
        <p className="question-label">
          {props.text}
        </p>
      </QButton>
    );
  }
  
  export default QuestionButton;