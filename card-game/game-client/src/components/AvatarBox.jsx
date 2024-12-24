import { Avatar } from "@mui/material";

function AvatarBox(props) {
  console.log(props.active);
  var borderColor = props.active ? "#0142ba": "gray";
  return (
    <div className="avatar-box">
      <Avatar 
        src={`/avatars/${props.n + 1}.png`} 
        sx={{ width: 56, height: 56 }}
        style={{ border: `0.1em solid ${borderColor}`}}
      >
        {props.userName[0]}
      </Avatar>
      <p className="avatar-label">
        {props.userName}  
      </p>
    </div>
  );
}

export default AvatarBox;